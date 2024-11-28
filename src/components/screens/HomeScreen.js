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
import { getUserName, getLatestSensorData, createAtualizacao, getLatestAtualizacao } from "../../services/api";
import { getToken } from "../../utils/Auth";

const PARAMETROS_LIMITE = {
  "Temperatura": { min: 18.0, max: 24.0 },
  "Ph": { min: 6.8, max: 7.2 },
  "Volume": { min: 45.0, max: 55.0 },
  "Oxigenacao": { min: 5.0, max: 8.0 }
};

export default function HomeScreen({ navigation }) {
  const [userName, setUserName] = useState("Usuário");
  const [isDropdownVisible, setDropdownVisible] = useState(false);
  const [selectedTank, setSelectedTank] = useState("Tanque 1");

  const [sensorData, setSensorData] = useState({
    temperatura: null,
    ph: null,
    oxigenacao: null,
    volume: null,
  });

  const [latestUpdate, setLatestUpdate] = useState(null);

  const tanks = ["Tanque 1", "Tanque 2"];

  const handleSelectTank = (tank) => {
    setSelectedTank(tank);
    setDropdownVisible(false);
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
    try {
      const sensorTypes = ["Temperatura", "Ph", "Oxigenacao", "Volume"];
      const tank = selectedTank;

      const fetchedData = {};

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
    try {
      const response = await getLatestAtualizacao(selectedTank);
      if (response?.data) {
        setLatestUpdate(response.data.mensagem || "Última atualização não encontrada.");
      }
    } catch (error) {
      setLatestUpdate("Erro ao buscar última atualização.");
    }
  };

  const createUpdate = async () => {
    try {
      await createAtualizacao(selectedTank);
      console.log(`Atualização criada para o ${selectedTank}`);
    } catch (error) {
      console.error("Erro ao criar atualização:", error);
    }
  };

const calculateProgress = (parametro, valor) => {
  const limite = PARAMETROS_LIMITE[parametro];

  if (valor === null || valor === undefined) {
    return 0;
  }


  if (valor < limite.min) {
    return 10;
  } else if (valor > limite.max) {
    return 100;
  } else {

    const progress = ((valor - limite.min) / (limite.max - limite.min)) * 100;

    const optimisticProgress = progress + 50;
    return optimisticProgress > 100 ? 100 : optimisticProgress;
  }
};


const calculateOverallProgress = () => {
  const { temperatura, ph, oxigenacao, volume } = sensorData;


  const tempProgress = calculateProgress("Temperatura", temperatura?.valor);
  const phProgress = calculateProgress("Ph", ph?.valor);
  const oxProgress = calculateProgress("Oxigenacao", oxigenacao?.valor);
  const volProgress = calculateProgress("Volume", volume?.valor);


  const overallProgress = (tempProgress + phProgress + oxProgress + volProgress) / 4;

  return overallProgress;
};

  useEffect(() => {
    fetchUserName();
    createUpdate();
  }, []);

  useEffect(() => {
    fetchSensorData();
    fetchLatestUpdate();
    createUpdate();
  }, [selectedTank]);

  return (
    <View style={styles.container}>
      {/* Cabeçalho */}
      <View style={styles.header}>
        <View style={styles.bar}>
          <Image
            source={require("../../assets/icons/icon_bar.png")}
            style={styles.logo}
          />
          <Text style={styles.textLogo}>Aquasense</Text>
        </View>

        <View style={styles.avatar}>
          <Text style={styles.greeting}>Olá, {userName}!</Text>

          {/* Botão de Dropdown */}
          <TouchableOpacity
            style={styles.tankSelector}
            onPress={() => setDropdownVisible(!isDropdownVisible)}
          >
            <Text style={styles.tankText}>{selectedTank}</Text>
            <Entypo
              name={isDropdownVisible ? "chevron-up" : "chevron-down"}
              size={20}
              color="#007BFF"
            />
          </TouchableOpacity>
        </View>

        {/* Dropdown */}
        {isDropdownVisible && (
          <View style={styles.dropdown}>
            <FlatList
              data={tanks}
              keyExtractor={(item, index) => index.toString()}
              renderItem={({ item }) => (
                <TouchableOpacity
                  style={styles.dropdownItem}
                  onPress={() => handleSelectTank(item)}
                >
                  <Text style={styles.dropdownText}>{item}</Text>
                </TouchableOpacity>
              )}
            />
          </View>
        )}

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
            <Text style={styles.progressText}>{`${Math.round(calculateOverallProgress())}%`}</Text>
          </View>
        </View>

      </View>

      {/* Cartões Informativos */}
      <View style={styles.infoContainer}>
        <Text style={styles.sectionTitle}>Água</Text>
        <View style={styles.card}>
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Oxigenação</Text>
            <Text style={styles.value}>
              {sensorData.oxigenacao ? `${sensorData.oxigenacao.valor} mg/L` : "Carregando..."}
            </Text>
          </View>
          <View style={styles.separator} />
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Volume</Text>
            <Text style={styles.value}>
              {sensorData.volume ? `${sensorData.volume.valor} Litros` : "Carregando..."}
            </Text>
          </View>
        </View>

        <Text style={styles.sectionTitle}>Condições Gerais</Text>
        <View style={styles.card}>
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Temp</Text>
            <Text style={styles.value}>
              {sensorData.temperatura ? `${sensorData.temperatura.valor}ºC` : "Carregando..."}
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

      {/* Barra de Navegação */}
      <View style={styles.toolBar}>
        <TouchableOpacity style={styles.iconButton}>
          <MaterialIcons name="description" size={24} color="#6C6C6C" />
        </TouchableOpacity>

        <TouchableOpacity style={[styles.iconButton, styles.centerButton]}>
          <MaterialIcons name="home" size={24} color="#FFFFFF" />
        </TouchableOpacity>

        <TouchableOpacity style={styles.iconButton}>
          <MaterialIcons name="tune" size={24} color="#6C6C6C" />
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#F9F9F9",
  },
  header: {
    backgroundColor: "#007BFF",
    paddingHorizontal: 20,
    borderBottomLeftRadius: 20,
    borderBottomRightRadius: 20,
    flexDirection: "column",
  },
  bar: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "flex-start",
    borderStyle: "solid",
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: "#F5F5F5",
    marginBottom: 15,
  },
  textLogo: {
    color: "#f5f5f5",
    fontFamily: "Montserrat_SemiBold",
    fontSize: 22,
  },
  logo: {
    width: 50,
    height: 50,
    marginRight: 20,
  },
  greeting: {
    fontSize: 24,
    width: 125,
    fontFamily: "Montserrat_Bold",
    color: "white",
  },

  avatar: {
    flexDirection: "row",
    justifyContent: "space-between",
    paddingRight: 20,
  },
  dropdown: {
    backgroundColor: "white",
    borderRadius: 10,
    padding: 10,
    marginTop: 5,
    elevation: 2,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  dropdownItem: {
    paddingVertical: 8,
    paddingHorizontal: 10,
    borderBottomWidth: 1,
    borderBottomColor: "#EAEAEA",
  },
  dropdownText: {
    fontSize: 16,
    color: "#007BFF",
    fontFamily: "Montserrat_Medium",
  },
  tankSelector: {
    backgroundColor: "white",
    height: 45,
    paddingVertical: 10,
    paddingHorizontal: 15,
    borderRadius: 10,
    marginTop: 5,
  },
  tankText: {
    fontSize: 16,
    color: "#007BFF",
    fontFamily: "Montserrat_SemiBold",
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
  toolBar: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "#FFFFFF",
    borderRadius: 25,
    padding: 10,
    width: "65%",
    alignSelf: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  iconButton: {
    width: 40,
    height: 40,
    justifyContent: "center",
    alignItems: "center",
  },
  centerButton: {
    backgroundColor: "#007BFF",
    width: 50,
    height: 50,
    borderRadius: 28,
    justifyContent: "center",
    alignItems: "center",
  },
});
