import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Dimensions,
  Alert,
} from "react-native";
import { LineChart } from "react-native-chart-kit";
import { getTanques, getLast10SensorData } from "../../../services/api";
import Header from "../../common/Header";
import Toolbar from "../../common/Toolbar";

const CustomButton = ({ title, onPress }) => (
  <TouchableOpacity style={styles.button} onPress={onPress}>
    <Text style={styles.buttonText}>{title}</Text>
  </TouchableOpacity>
);

const GraphScreen = () => {
  const [tanques, setTanques] = useState([]);
  const [selectedTank, setSelectedTank] = useState(null);
  const [sensorData, setSensorData] = useState(null);

  const screenWidth = Dimensions.get("window").width;


  useEffect(() => {
    const fetchTanques = async () => {
      try {
        const response = await getTanques();
        setTanques(response);
      } catch (error) {
        Alert.alert("Erro", "Erro ao carregar a lista de tanques.");
      }
    };

    fetchTanques();
  }, []);


  const fetchSensorData = async (tankName) => {
    try {
      const sensorTypes = ["Temperatura", "Ph", "Tds", "Volume"];
      const fetchedData = {
        temperatura: [],
        ph: [],
        tds: [],
        volume: [],
        timestamps: [],
      };
  
      for (const type of sensorTypes) {
        const response = await getLast10SensorData(type, tankName);
  
        // Log para verificar a resposta da API
        console.log(`Resposta da API para ${type}:`, response);
  

        if (response && response.data && Array.isArray(response.data.data) && response.data.data.length > 0) {

          fetchedData[type.toLowerCase()] = response.data.data.map(item => item.valor);
          fetchedData.timestamps = response.data.data.map(item => new Date(item.data).toLocaleString());
        } else {
          // Adiciona uma mensagem de aviso somente se não houver dados
          console.warn(`Nenhum dado encontrado para o sensor: ${type} no tanque ${tankName}`);
        }
      }
  
      setSensorData(fetchedData);
    } catch (error) {
      console.error("Erro ao buscar dados do sensor:", error);
    }
  };
 

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Header />

      {!selectedTank ? (
        <View style={styles.form}>
          <Text style={styles.title}>Selecione um Tanque</Text>
          {tanques.map((tank) => (
            <CustomButton
              key={tank.name}
              title={tank.name}
              onPress={() => {
                setSelectedTank(tank.name);
                fetchSensorData(tank.name);
              }}
            />
          ))}
        </View>
      ) : (
        <View style={styles.chartContainer}>
          <Text style={styles.title}>Gráficos do Tanque: {selectedTank}</Text>

          {sensorData && (
            <>
              {/* Gráfico de Temperatura */}
              <View style={styles.card}>
                <Text style={styles.cardTitle}>Temperatura (°C)</Text>
                <LineChart
                  data={{
                    labels: sensorData.timestamps,
                    datasets: [{ data: sensorData.temperatura }],
                  }}
                  width={screenWidth - 50}
                  height={200}
                  chartConfig={chartConfig}
                  style={styles.chart}
                />
              </View>

              {/* Gráfico de pH */}
              <View style={styles.card}>
                <Text style={styles.cardTitle}>pH</Text>
                <LineChart
                  data={{
                    labels: sensorData.timestamps,
                    datasets: [{ data: sensorData.ph }],
                  }}
                  width={screenWidth - 50}
                  height={200}
                  chartConfig={chartConfig}
                  style={styles.chart}
                />
              </View>

              {/* Gráfico de TDS */}
              <View style={styles.card}>
                <Text style={styles.cardTitle}>TDS (ppm)</Text>
                <LineChart
                  data={{
                    labels: sensorData.timestamps,
                    datasets: [{ data: sensorData.tds }],
                  }}
                  width={screenWidth - 50}
                  height={200}
                  chartConfig={chartConfig}
                  style={styles.chart}
                />
              </View>

              {/* Gráfico de Volume */}
              <View style={styles.card}>
                <Text style={styles.cardTitle}>Volume (L)</Text>
                <LineChart
                  data={{
                    labels: sensorData.timestamps,
                    datasets: [{ data: sensorData.volume }],
                  }}
                  width={screenWidth - 50}
                  height={200}
                  chartConfig={chartConfig}
                  style={styles.chart}
                />
              </View>
            </>
          )}

          <TouchableOpacity
            style={styles.backButton}
            onPress={() => {
              setSelectedTank(null); // Volta para a seleção de tanques
              setSensorData(null); // Limpa os dados
            }}
          >
            <Text style={styles.backButtonText}>Voltar</Text>
          </TouchableOpacity>
        </View>
      )}

      <Toolbar />
    </ScrollView>
  );
};

const chartConfig = {
  backgroundGradientFrom: "#fff",
  backgroundGradientTo: "#fff",
  decimalPlaces: 1,
  color: (opacity = 1) => `rgba(34, 202, 236, ${opacity})`,
  labelColor: (opacity = 1) => `rgba(0, 0, 0, ${opacity})`,
  style: {
    borderRadius: 16,
  },
};

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    backgroundColor: "#f5f5f5",
  },
  form: {
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontFamily: "Montserrat_Bold",
    color: "#007BFF",
    marginBottom: 20,
  },
  button: {
    backgroundColor: "#FFFFFF",
    padding: 16,
    borderRadius: 30,
    marginVertical: 8,
    elevation: 2,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
  },
  buttonText: {
    textAlign: "center",
    fontSize: 16,
    color: "#23272A",
  },
  chartContainer: {
    padding: 12,
    paddingBottom: 115,
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 16,
    padding: 16,
    marginBottom: 20,
    elevation: 3,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
  },
  cardTitle: {
    fontSize: 18,
    fontFamily: "Montserrat_SemiBold",
    color: "#333",
    marginBottom: 10,
  },
  chart: {
    borderRadius: 16,
  },
  backButton: {
    backgroundColor: "#ff5c5c",
    padding: 15,
    borderRadius: 10,
    alignItems: "center",
    marginTop: 20,
  },
  backButtonText: {
    fontSize: 18,
    color: "#fff",
    fontFamily: "Montserrat_SemiBold",
  },
});

export default GraphScreen;
