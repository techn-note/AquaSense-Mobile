import React from "react";
import { View, TouchableOpacity, StyleSheet, Dimensions } from "react-native";
import { MaterialIcons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";

const Toolbar = () => {
  const navigation = useNavigation();

  return (
    <View style={styles.toolBar}>
      {/* Botão para a tela "Description" */}
      <TouchableOpacity
        style={styles.iconButton}
        onPress={() => navigation.navigate("DataScreen")}
      >
        <MaterialIcons name="description" size={24} color="#6C6C6C" />
      </TouchableOpacity>

      {/* Botão central para a tela "Home" */}
      <TouchableOpacity
        style={[styles.iconButton, styles.centerButton]}
        onPress={() => navigation.navigate("HomeScreen")}
      >
        <MaterialIcons name="home" size={24} color="#FFFFFF" />
      </TouchableOpacity>

      {/* Botão para a tela "Settings" */}
      <TouchableOpacity
        style={styles.iconButton}
        onPress={() => navigation.navigate("SettingsScreen")}
      >
        <MaterialIcons name="tune" size={24} color="#6C6C6C" />
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
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
    position: "absolute",
    bottom: Dimensions.get("window").height * 0.0375, // 3.75% da altura da tela
    position: 'absolute'
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

export default Toolbar;
