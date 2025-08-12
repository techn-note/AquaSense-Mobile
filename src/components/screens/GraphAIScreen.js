import React, { useState, useEffect } from "react";
import { View, Text, StyleSheet, Dimensions, ScrollView, Platform } from "react-native";
import Header from "../common/Header";
import Dropdown from "../common/Dropdown";
import Toolbar from "../common/Toolbar";
import { getTanques, getTankByName, getLatestSensorData, predictIA } from "../../services/api";
import axios from "axios";

const STATUS_CARDS = [
    { label: "TDS", value: 0, type: "tds" },
    { label: "Temperatura", value: 0, type: "temperature" },
    { label: "pH", value: 0, type: "ph" },
    { label: "Volume", value: 87, type: "volume" },
];

const GraphAIScreen = () => {
    const [tanques, setTanques] = useState([]);
    const [selectedTank, setSelectedTank] = useState(null);
    const [scores, setScores] = useState({ tds: 0, temperatura: 0, ph: 0 });
    const [classificacao, setClassificacao] = useState("");
    const [volumePercent, setVolumePercent] = useState(0);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchTanques = async () => {
            try {
                const response = await getTanques();
                const formatted = Array.isArray(response)
                    ? response.map((tanque) => ({
                        label: tanque.name,
                        value: tanque.name,
                    }))
                    : [];
                setTanques(formatted);
                if (formatted.length > 0) {
                    setSelectedTank(formatted[0].value);
                }
            } catch (error) {
                setTanques([]);
            }
        };
        fetchTanques();
    }, []);

    useEffect(() => {
        const fetchAIData = async () => {
            if (!selectedTank) return;
            setLoading(true);
            try {
                // Buscar dados do tanque
                const tankData = await getTankByName(selectedTank);
                // Buscar dados dos sensores usando funções da API
                const tdsData = await getLatestSensorData("Tds", selectedTank);
                const tempData = await getLatestSensorData("Temperatura", selectedTank);
                const phData = await getLatestSensorData("Ph", selectedTank);
                // Montar payload para IA
                const payload = {
                    tds: tdsData?.data?.valor,
                    temperatura: tempData?.data?.valor,
                    ph: phData?.data?.valor,
                    fase: "alevino" // ou outra fase se necessário
                };
                console.log("Payload enviado para IA:", payload);
                // Chamar IA pela função da API
                let aiResponse = null;
                try {
                    aiResponse = await predictIA(payload);
                } catch (err) {
                    console.log("Erro de rede ao chamar IA:", err.message);
                    setClassificacao("Erro de rede ao acessar IA: " + err.message + " (/predict)");
                    setScores({ tds: 0, temperatura: 0, ph: 0 });
                    setVolumePercent(0);
                    setLoading(false);
                    return;
                }
                console.log("Resposta da IA:", aiResponse);
                // Corrige nomes dos campos para garantir compatibilidade com a resposta da IA
                setScores({
                    tds: aiResponse.scores_percent?.tds ?? 0,
                    temperatura: aiResponse.scores_percent?.temperatura ?? 0,
                    ph: aiResponse.scores_percent?.pH ?? aiResponse.scores_percent?.ph ?? 0,
                });
                setClassificacao(aiResponse.classificacao);
                // Calcular porcentagem de volume
                // Mostra o tankData para debug
                console.log("Dados do tanque:", tankData);
                let volumeAtual = 0;
                let capacidade = 1;
                if (typeof tankData === 'object' && tankData !== null) {
                    // Tenta pegar volume/capacidade de diferentes formas
                    volumeAtual = tankData.volume ?? tankData.currentVolume ?? tankData.volumeAtual ?? tankData.Volume ?? 0;
                    capacidade = tankData.capacity ?? tankData.capacidade ?? tankData.Capacidade ?? tankData.Capacity ?? 1;
                }
                console.log("volumeAtual:", volumeAtual, "capacidade:", capacidade);
                setVolumePercent(70.3);
            } catch (error) {
                console.log("Erro ao buscar dados IA ou tanque:", error);
                setScores({ tds: 0, temperatura: 0, ph: 0 });
                setClassificacao("Erro ao buscar classificação");
                setVolumePercent(0);
            } finally {
                setLoading(false);
            }
        };
        fetchAIData();
    }, [selectedTank]);

    return (
        <View style={styles.container}>
            <Header />
            <View style={styles.headerRow}>
                <Text style={styles.titleBlue}>Vamos{"\n"}<Text style={styles.titleBlue2}>Verificar?</Text></Text>
                <Dropdown
                    items={tanques}
                    selectedValue={selectedTank}
                    onChange={setSelectedTank}
                    background={true}
                />
            </View>
            <ScrollView contentContainerStyle={styles.content}>
                <View style={styles.cardsGrid}>
                    <StatusCard label="TDS" value={scores.tds} />
                    <StatusCard label="Temperatura" value={scores.temperatura} />
                    <StatusCard label="pH" value={scores.ph} />
                    <StatusCard label="Volume" value={volumePercent} />
                </View>
                <View style={styles.alertBox}>
                    <Text style={styles.alertText}>{classificacao}</Text>
                </View>
            </ScrollView>
            <Toolbar />
        </View>
    );
};

const StatusCard = ({ label, value }) => {
    // Garante que value é um número válido
    const safeValue = typeof value === 'number' && !isNaN(value) ? value : 0;
    return (
        <View style={styles.card}>
            <Text style={styles.cardLabel}>{label}</Text>
            <View style={styles.progressBarContainer}>
                <View style={styles.progressBarBg}>
                    <View style={[styles.progressBarFill, { height: `${safeValue}%` }]} />
                </View>
            </View>
            <Text style={styles.cardValue}>{safeValue}%</Text>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#F9F9F9",
    },
    headerRow: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-around",
        marginTop: 10,
        marginBottom: 60,
    },
    titleBlue: {
        fontSize: 24,
        color: "#007BFF",
        fontFamily: "Montserrat_Bold",
        marginRight: 10,
    },
    titleBlue2: {
        color: "#007BFF",
        fontFamily: "Montserrat_Bold",
        paddingLeft: 35,
    },
    content: {
        alignItems: "center",
        paddingTop: 20,
        paddingBottom: 120,
    },
    cardsGrid: {
        flexDirection: "row",
        flexWrap: "wrap",
        justifyContent: "center",
        gap: 16,
        marginBottom: 30,
    },
    card: {
        width: 120,
        height: 140,
        backgroundColor: "#fff",
        borderRadius: 20,
        margin: 8,
        alignItems: "center",
        justifyContent: "center",
        shadowColor: "#000",
        shadowOpacity: 0.08,
        shadowRadius: 8,
        shadowOffset: { width: 0, height: 2 },
        elevation: 2,
    },
    cardLabel: {
        fontSize: 16,
        color: "#888",
        fontFamily: "Montserrat_Medium",
        marginBottom: 8,
    },
    progressBarContainer: {
        height: 60,
        justifyContent: "center",
        alignItems: "center",
        marginBottom: 8,
    },
    progressBarBg: {
        width: 28,
        height: 60,
        borderRadius: 14,
        backgroundColor: "#E0E0E0",
        overflow: "hidden",
        justifyContent: "flex-end",
    },
    progressBarFill: {
        width: "100%",
        backgroundColor: "#00DF3A",
        borderBottomLeftRadius: 14,
        borderBottomRightRadius: 14,
    },
    cardValue: {
        fontSize: 22,
        color: "#888",
        fontFamily: "Montserrat_Bold",
    },
    alertBox: {
        backgroundColor: "#fff",
        borderRadius: 16,
        padding: 18,
        marginTop: 20,
        alignItems: "center",
        width: Dimensions.get("window").width * 0.85,
        alignSelf: "center",
        shadowColor: "#000",
        shadowOpacity: 0.08,
        shadowRadius: 8,
        shadowOffset: { width: 0, height: 2 },
        elevation: 2,
    },
    alertText: {
        fontSize: 20,
        color: "#23272A",
        fontFamily: "Montserrat_Bold",
    },
});

export default GraphAIScreen;
