import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  Image,
  TouchableOpacity,
  FlatList,
} from "react-native";
import { ProgressBar } from "react-native-paper";
import { MaterialIcons, Entypo } from "@expo/vector-icons";
import {
  getUserName,
  getLatestSensorData,
  createAtualizacao,
  getLatestAtualizacao,
  getTanques,
} from "../../services/api";
import { getToken } from "../../utils/Auth";
import Dropdown from "../common/Dropdown";
import Header from "../common/Header";
import Toolbar from "../common/Toolbar";

const PARAMETROS_LIMITE = {
  Temperatura: { min: 18.0, max: 24.0 },
  Ph: { min: 6.8, max: 7.2 },
  Volume: { min: 45.0, max: 55.0 },
  Tds: { min: 150, max: 300 },
};

export default function HomeScreen({ navigation }) {
  const [userName, setUserName] = useState("Usuário");
  const [selectedTank, setSelectedTank] = useState(null);
  const [formattedTanques, setFormattedTanques] = useState([]);
  const [sensorData, setSensorData] = useState({
    temperatura: null,
    ph: null,
    tds: null,
    volume: null,
  });
  const [latestUpdate, setLatestUpdate] = useState(null);

  const fetchTanques = async () => {
    try {
      const response = await getTanques();
      const tanques = response?.data?.data || [];
      if (!Array.isArray(tanques)) {
        throw new Error("O formato dos dados de tanques é inválido.");
      }

      const formatted = tanques.map((tanque) => ({
        label: tanque.name,
        value: tanque.name,
      }));

      setFormattedTanques(formatted);
      if (formatted.length > 0) {
        console.log(`Tanque selecionado: ${formatted[0].value}`);
        setSelectedTank(formatted[0].value);
      }
    } catch (error) {
      console.error("Erro ao buscar tanques:", error);
    }
  };

  

  const fetchUserName = async () => {
    try {
      const token = await getToken();
      if (token) {
        const name = await getUserName(token);
        setUserName(name);
      } else {
        setUserName("Usuário não autenticado");
      }
    } catch (error) {
      console.error("Erro ao buscar nome do usuário:", error);
      setUserName("Usuário");
    }
  };

  const fetchSensorData = async () => {
    if (!selectedTank) return;
    try {
      const sensorTypes = ["Temperatura", "Ph", "Tds", "Volume"];
      const fetchedData = {};

      const tank = selectedTank.toLowerCase()

      for (const type of sensorTypes) {
        const response = await getLatestSensorData(type, tank);
        if (response?.data) {
          fetchedData[type.toLowerCase()] = response.data;
        }
      }

      setSensorData(fetchedData);
    } catch (error) {
      console.error("Erro ao buscar dados do sensor:", error);
    }
  };

  const fetchLatestUpdate = async () => {
    if (!selectedTank) return;
    try {
      const response = await getLatestAtualizacao(selectedTank);
      setLatestUpdate(
        response?.data?.mensagem || "Última atualização não encontrada."
      );
    } catch (error) {
      setLatestUpdate("Erro ao buscar última atualização.");
    }
  };

  const createUpdate = async () => {
    if (!selectedTank) return;
    try {
      await createAtualizacao(selectedTank);
      console.log(`Atualização criada para o ${selectedTank}`);
    } catch (error) {
      console.error("Erro ao criar atualização:", error);
    }
  };

  const handleTankChange = (newValue) => {
    setSelectedTank(newValue);
  };

  useEffect(() => {
    fetchTanques();
    fetchUserName();
  }, []);

  useEffect(() => {
    if (selectedTank) {
      fetchSensorData();
      fetchLatestUpdate();
      createUpdate();
    }
  }, [selectedTank]);

  const calculateProgress = (parametro, valor) => {
    const limite = PARAMETROS_LIMITE[parametro];
    if (valor === null || valor === undefined) return 0;

    if (valor < limite.min) return 10;
    if (valor > limite.max) return 100;

    const progress = ((valor - limite.min) / (limite.max - limite.min)) * 100;
    return Math.min(progress + 50, 100);
  };

  const calculateOverallProgress = () => {
    const { temperatura, ph, tds, volume } = sensorData;
    const tempProgress = calculateProgress("Temperatura", temperatura?.valor);
    const phProgress = calculateProgress("Ph", ph?.valor);
    const tdsProgress = calculateProgress("Tds", tds?.valor);
    const volProgress = calculateProgress("Volume", volume?.valor);
    return (tempProgress + phProgress + tdsProgress + volProgress) / 4;
  };
  return (
    <View style={styles.container}>
      {/* Cabeçalho */}
      <Header>
        {/* Avatar e Dropdown */}
        <View style={styles.avatar}>
          <Text style={styles.greeting}>Olá, {userName}!</Text>
          <Dropdown
            items={formattedTanques}
            selectedValue={selectedTank}
            onChange={handleTankChange}
          />
        </View>

        {/* Status Principal */}
        <View style={styles.statusContainer}>
          {latestUpdate === "Tudo certo por aqui!" ? (
            <MaterialIcons name="thumb-up" size={60} color="white" />
          ) : (
            <MaterialIcons name="cancel" size={60} color="white" />
          )}
          <Text style={styles.statusText}>{latestUpdate}</Text>
          <View style={styles.ProgressBarContainer}>
            <ProgressBar
              progress={calculateOverallProgress() / 100}
              color={calculateOverallProgress() >= 80 ? "#00DF3A" : "#FF5733"}
              style={styles.progressBar}
            />
            <Text style={styles.progressText}>{`${Math.round(
              calculateOverallProgress()
            )}%`}</Text>
          </View>
        </View>
      </Header>

      {/* Cartões Informativos */}
      <View style={styles.infoContainer}>
        <Text style={styles.sectionTitle}>Água</Text>
        <View style={styles.card}>
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Tds</Text>
            <Text style={styles.value}>
              {sensorData.tds ? `${sensorData.tds.valor} PPM` : "Carregando..."}
            </Text>
          </View>
          <View style={styles.separator} />
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Volume</Text>
            <Text style={styles.value}>
              {sensorData.volume
                ? `${sensorData.volume.valor} Litros`
                : "Carregando..."}
            </Text>
          </View>
        </View>

        <Text style={styles.sectionTitle}>Condições Gerais</Text>
        <View style={styles.card}>
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Temp</Text>
            <Text style={styles.value}>
              {sensorData.temperatura
                ? `${sensorData.temperatura.valor}ºC`
                : "Carregando..."}
            </Text>
          </View>
          <View style={styles.separator} />
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Ph</Text>
            <Text style={styles.value}>
              {sensorData.ph ? `${sensorData.ph.valor}` : "Carregando..."}
            </Text>
          </View>
        </View>
      </View>

      <Toolbar />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#F9F9F9",
    height: "100%",
  },
  greeting: {
    fontSize: 24,
    fontFamily: "Montserrat_Bold",
    color: "white",
  },
  avatar: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
  statusContainer: {
    alignItems: "center",
    padding: 20,
  },
  statusText: {
    fontSize: 20,
    color: "white",
    fontFamily: "Montserrat_Bold",
    marginTop: 10,
  },
  ProgressBarContainer: {
    marginTop: 15,
    position: "relative",
  },
  progressBar: {
    width: 325,
    height: 36,
    borderRadius: 15,
  },
  progressText: {
    position: "absolute",
    alignSelf: "center",
    fontSize: 24,
    fontFamily: "Montserrat_Bold",
    color: "#fff",
  },

  infoContainer: {
    padding: 20,
  },
  sectionTitle: {
    fontSize: 24,
    color: "#007BFF",
    fontFamily: "Montserrat_SemiBold",
    marginBottom: 10,
  },
  card: {
    flexDirection: "row",
    justifyContent: "space-around",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    padding: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
    width: "100%",
    alignSelf: "center",
  },
  dataContainer: {
    alignItems: "center",
  },
  label: {
    color: "#6C6C6C",
    fontSize: 16,
    fontFamily: "Montserrat_Medium",
  },
  value: {
    color: "#007BFF",
    fontSize: 16,
    fontFamily: "Montserrat_Bold",
    marginTop: 4,
  },
  separator: {
    width: 3,
    borderRadius: 5,
    height: "60%",
    backgroundColor: "#007BFF",
  },
});
