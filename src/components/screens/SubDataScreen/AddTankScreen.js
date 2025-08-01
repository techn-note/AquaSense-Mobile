import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  StyleSheet,
  TouchableOpacity,
  Alert,
} from "react-native";
import Header from "../../common/Header";
import Toolbar from "../../common/Toolbar";
import { createTank } from "../../../services/api";

const AddTankScreen = ({ navigation }) => {
  const [name, setName] = useState("");
  const [capacity, setCapacity] = useState("");
  const [number, setNumber] = useState("");

  const handleSave = async () => {
    // Validações básicas
    if (name.length === 0 || name.length > 10) {
      Alert.alert("Erro", "O nome deve ter entre 1 e 10 caracteres.");
      return;
    }
    if (!capacity || isNaN(capacity) || capacity <= 0) {
      Alert.alert("Erro", "A capacidade deve ser um número maior que 0.");
      return;
    }
    if (!number || isNaN(number) || number <= 0) {
      Alert.alert("Erro", "O número do tanque deve ser um número maior que 0.");
      return;
    }

    const tankData = {
      name,
      capacity,
      number: parseInt(number, 10),
    };

    try {
      const response = await createTank(tankData);

      if (response && response.data.message === "Tank created successfully") {
        Alert.alert("Sucesso", "Tanque adicionado com sucesso!");

        setName("");
        setCapacity("");
        setNumber("");

        navigation.navigate("HomeScreen");
      } else {
        Alert.alert("Erro", "Ocorreu um erro ao adicionar o tanque.");
      }
    } catch (error) {
      console.error("Erro ao criar tanque:", error);
      Alert.alert("Erro", "Erro ao criar tanque. Tente novamente.");
    }
  };

  return (
    <View style={styles.container}>
      <Header />

      <View style={styles.form}>
        <Text style={styles.title}>Adicionar Tanque</Text>

        {/* Campo Nome */}
        <TextInput
          style={styles.input}
          placeholder="Nome (máx. 10 caracteres)"
          maxLength={10}
          value={name}
          onChangeText={setName}
        />

        {/* Campo Capacidade */}
        <TextInput
          style={styles.input}
          placeholder="Capacidade (Litros)"
          keyboardType="numeric"
          value={capacity}
          onChangeText={setCapacity}
        />

        {/* Campo Número do Tanque */}
        <TextInput
          style={styles.input}
          placeholder="Número do Tanque"
          keyboardType="numeric"
          value={number}
          onChangeText={setNumber}
        />

        {/* Botão de Salvar */}
        <TouchableOpacity style={styles.saveButton} onPress={handleSave}>
          <Text style={styles.saveButtonText}>Salvar</Text>
        </TouchableOpacity>
      </View>
      <Toolbar />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#f5f5f5",
    height: "100%",
  },
  form: {
    marginTop: 100,
    padding: 45,
  },
  title: {
    fontSize: 24,
    fontFamily: "Montserrat_Bold",
    color: "#007BFF",
    marginBottom: 20,
  },
  input: {
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 10,
    padding: 10,
    marginBottom: 15,
    fontSize: 16,
    fontFamily: "Montserrat_Regular",
    backgroundColor: "#fff",
  },
  saveButton: {
    backgroundColor: "#007BFF",
    padding: 15,
    borderRadius: 10,
    alignItems: "center",
  },
  saveButtonText: {
    fontSize: 18,
    color: "#fff",
    fontFamily: "Montserrat_SemiBold",
  },
});

export default AddTankScreen;
