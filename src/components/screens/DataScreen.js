import React, { useState } from "react";
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
} from "react-native";
import Header from "../common/Header";
import Toolbar from "../common/Toolbar";
import SegmentedButtons from "../common/SegmentedButton"; // Import do componente animado

export default function DataScreen({ navigation }) {
  const [selectedOption, setSelectedOption] = useState("Tanque");

  const renderButtons = () => {
    return selectedOption === "Tanque" ? (
      <>
        <CustomButton
          title="Gráficos"
          onPress={() => navigation.navigate("GraphScreen")}
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
    ) : (
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
  };

  return (
    <View style={styles.container}>
      {/* Header */}
      <Header />
      {/* Segmented Buttons */}
      <SegmentedButtons
        options={["Tanque", "Peixe"]}
        selectedOption={selectedOption}
        onSelect={setSelectedOption}
      />
      <ScrollView key={selectedOption} style={styles.content}>
        {renderButtons()}
      </ScrollView>

      <Toolbar/>
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
  content: {
    marginTop: 16,
    paddingHorizontal: 30,
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
    textAlign: "start",
    fontSize: 16,
    color: "#23272A",
  },
});
