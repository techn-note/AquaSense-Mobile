import React, { useState, useEffect } from "react";
import {
  View,
  TouchableOpacity,
  StyleSheet,
  Dimensions,
  Image,
} from "react-native";
import { useNavigation, useRoute } from "@react-navigation/native";

const Toolbar = () => {
  const [selectedIcon, setSelectedIcon] = useState("home"); // Default to home
  const navigation = useNavigation();
  const route = useRoute(); // Pegando a rota atual

  useEffect(() => {
    // Verificando qual a tela ativa e definindo o ícone selecionado
    switch (route.name) {
      case "HomeScreen":
        setSelectedIcon("home");
        break;
      case "DefinitionsScreen":
        setSelectedIcon("definitions");
        break;
      case "DataScreen":
        setSelectedIcon("document");
        break;
      default:
        setSelectedIcon("home");
    }
  }, [route.name]); // Atualiza quando a tela muda

  const handleIconPress = (icon, screen) => {
    setSelectedIcon(icon);
    navigation.navigate(screen); // Navega para a tela correspondente
  };

  return (
    <View style={styles.toolBar}>
      {/* Botão para a tela "DataScreen" */}
      <TouchableOpacity
        style={[styles.iconButton, selectedIcon === "document" && styles.selectedButton]}
        onPress={() => handleIconPress("document", "DataScreen")}
      >
        <Image
          source={
            selectedIcon === "document"
              ? require("../../assets/icons/icon_document_1.png")
              : require("../../assets/icons/icon_document_0.png")
          }
          style={styles.iconImage}
        />
      </TouchableOpacity>

      {/* Botão central para a tela "HomeScreen" */}
      <TouchableOpacity
        style={[styles.iconButton, selectedIcon === "home" && styles.selectedButton]}
        onPress={() => handleIconPress("home", "HomeScreen")}
      >
        <Image
          source={
            selectedIcon === "home"
              ? require("../../assets/icons/icon_home_1.png")
              : require("../../assets/icons/icon_home_0.png")
          }
          style={styles.iconImage}
        />
      </TouchableOpacity>

      {/* Botão para a tela "DefinitionsScreen" */}
      <TouchableOpacity
        style={[styles.iconButton, selectedIcon === "definitions" && styles.selectedButton]}
        onPress={() => handleIconPress("definitions", "DefinitionsScreen")}
      >
        <Image
          source={
            selectedIcon === "definitions"
              ? require("../../assets/icons/Icon_definitions_1.png")
              : require("../../assets/icons/Icon_definitions_0.png")
          }
          style={styles.iconImage}
        />
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
  },
  iconButton: {
    width: 40,
    height: 40,
    justifyContent: "center",
    alignItems: "center",
  },
  selectedButton: {
    backgroundColor: "#007BFF", // Cor de destaque
    width: 50,
    height: 50,
    borderRadius: 28,
    justifyContent: "center",
    alignItems: "center",
  },
  iconImage: {
    width: 24,
    height: 24,
  },
});

export default Toolbar;
