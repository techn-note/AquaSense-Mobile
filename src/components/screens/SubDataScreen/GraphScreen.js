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

        if (
          response &&
          response &&
          Array.isArray(response) &&
          response.length > 0
        ) {
          fetchedData[type.toLowerCase()] = response.map(
            (item) => item.valor
          );
          fetchedData.timestamps = response.map((item) => {
            const date = new Date(item.data);
            const hours = date.getHours().toString().padStart(2, '0'); 
            const minutes = date.getMinutes().toString().padStart(2, '0');
            return `${hours}h${minutes}`;
          });
        } else {
          console.warn(`Nenhum dado encontrado para o sensor: ${type} no tanque ${tankName}`);
        }
      }

      setSensorData(fetchedData);
      console.log(fetchedData)
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
              <SensorCard
                title="Temperatura (°C)"
                data={sensorData.temperatura}
                timestamps={sensorData.timestamps}
                screenWidth={screenWidth}
              />

              {/* Gráfico de pH */}
              <SensorCard
                title="pH"
                data={sensorData.ph}
                timestamps={sensorData.timestamps}
                screenWidth={screenWidth}
              />

              {/* Gráfico de TDS */}
              <SensorCard
                title="TDS (ppm)"
                data={sensorData.tds}
                timestamps={sensorData.timestamps}
                screenWidth={screenWidth}
              />

              {/* Gráfico de Volume */}
              <SensorCard
                title="Volume (L)"
                data={sensorData.volume}
                timestamps={sensorData.timestamps}
                screenWidth={screenWidth}
              />
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

const SensorCard = ({ title, data, timestamps, screenWidth }) => {
  return data && data.length > 0 ? (
    <View style={styles.card}>
      <Text style={styles.cardTitle}>{title}</Text>
      <LineChart
        data={{
          labels: timestamps,
          datasets: [{ data }],
        }}
        width={screenWidth - 50}
        height={200}
        chartConfig={chartConfig}
        style={styles.chart}
      />
    </View>
  ) : (
    <View style={styles.card}>
      <Text style={styles.cardTitle}>{title}</Text>
      <Text style={styles.noDataText}>Nenhum dado disponível no momento.</Text>
    </View>
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
  noDataText: {
    fontSize: 16,
    fontStyle: "italic",
    color: "#888",
    textAlign: "center",
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
