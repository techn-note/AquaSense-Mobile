import React, { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
} from "react-native";
import Header from "../common/Header";
Header;
import Toolbar from "../common/Toolbar";

export default function DataScreen({ navigation }) {
  const [selectedOption, setSelectedOption] = useState("Tanque");

  const renderButtons = () => {
    if (selectedOption === "Tanque") {
      return (
        <>
          <CustomButton
            title="Gráficos"
            onPress={() => navigation.navigate("GraphsScreen")}
          />
          <CustomButton
            title="Atualizar Tanque"
            onPress={() => navigation.navigate("UpdateTankScreen")}
          />
          <CustomButton
            title="Cadastrar Tanque"
            onPress={() => navigation.navigate("AddTankScreen")}
          />
        </>
      );
    } else {
      return (
        <>
          <CustomButton
            title="Atualizar Peixe"
            onPress={() => navigation.navigate("UpdateFishScreen")}
          />
          <CustomButton
            title="Cadastrar Peixe"
            onPress={() => navigation.navigate("AddFishScreen")}
          />
        </>
      );
    }
  };

  return (
    <View style={styles.container}>
      {/* Header */}
      <Header />

      {/* Content */}
      <View style={styles.toggleButtons}>
        <TouchableOpacity
          style={[
            styles.toggleButton,
            selectedOption === "Tanque" && styles.activeButton,
          ]}
          onPress={() => setSelectedOption("Tanque")}
        >
          <Text
            style={[
              styles.toggleButtonText,
              selectedOption === "Tanque" && styles.activeText,
            ]}
          >
            Tanque
          </Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[
            styles.toggleButton,
            selectedOption === "Peixe" && styles.activeButton,
          ]}
          onPress={() => setSelectedOption("Peixe")}
        >
          <Text
            style={[
              styles.toggleButtonText,
              selectedOption === "Peixe" && styles.activeText,
            ]}
          >
            Peixe
          </Text>
        </TouchableOpacity>
      </View>

      <ScrollView style={styles.content}>{renderButtons()}</ScrollView>
      <Toolbar />
    </View>
  );
}

const CustomButton = ({ title, onPress }) => (
  <TouchableOpacity style={styles.button} onPress={onPress}>
    <Text style={styles.buttonText}>{title}</Text>
  </TouchableOpacity>
);

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F9F9F9",
    height: "100%",
  },
  header: {
    backgroundColor: "#007BFF",
    padding: 16,
    borderBottomLeftRadius: 24,
    borderBottomRightRadius: 24,
    alignItems: "center",
  },
  logo: {
    color: "#FFFFFF",
    fontSize: 20,
    fontWeight: "bold",
  },
  toggleButtons: {
    flexDirection: "row",
    marginTop: 16,
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    overflow: "hidden",
  },
  toggleButton: {
    paddingVertical: 8,
    paddingHorizontal: 24,
    flex: 1,
    alignItems: "center",
  },
  toggleButtonText: {
    color: "#007BFF",
    fontWeight: "bold",
  },
  activeButton: {
    backgroundColor: "#007BFF",
  },
  activeText: {
    color: "#FFFFFF",
  },
  content: {
    marginTop: 16,
    paddingHorizontal: 16,
  },
  button: {
    backgroundColor: "#FFFFFF",
    padding: 16,
    borderRadius: 12,
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
    color: "#007BFF",
  },
  footer: {
    flexDirection: "row",
    justifyContent: "space-around",
    padding: 16,
    backgroundColor: "#FFFFFF",
    borderTopWidth: 1,
    borderTopColor: "#E0E0E0",
  },
  iconButton: {
    padding: 12,
    backgroundColor: "#F9F9F9",
    borderRadius: 24,
    alignItems: "center",
    justifyContent: "center",
    elevation: 1,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
  },
});
