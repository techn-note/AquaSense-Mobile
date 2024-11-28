import React, { useState } from "react";
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

export default function HomeScreen({ navigation }) {
  const [isDropdownVisible, setDropdownVisible] = useState(false);
  const [selectedTank, setSelectedTank] = useState("Tanque 1");

  const tanks = ["Tanque 1", "Tanque 2", "Tanque 3"];

  const handleSelectTank = (tank) => {
    setSelectedTank(tank);
    setDropdownVisible(false);
    // Aqui você pode enviar o `tank` para a API
    console.log("Tanque selecionado:", tank);
  };

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
          <Text style={styles.greeting}>Olá, Rodrigo!</Text>

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
          <MaterialIcons name="thumb-up" size={60} color="white" />
          <Text style={styles.statusText}>Tudo certo por Aqui!</Text>
          <View style={styles.ProgressBarContainer}>
            <ProgressBar
              progress={0.98}
              color="#00DF3A"
              style={styles.progressBar}
            />
            <Text style={styles.progressText}>98%</Text>
          </View>
        </View>
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
