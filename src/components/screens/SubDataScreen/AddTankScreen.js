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

const AddTankScreen = () => {
  const [name, setName] = useState("");
  const [capacity, setCapacity] = useState("");
  const [value, setValue] = useState("");

  const handleSave = () => {
    // Validações básicas
    if (name.length === 0 || name.length > 10) {
      Alert.alert("Erro", "O nome deve ter entre 1 e 10 caracteres.");
      return;
    }
    if (!capacity || isNaN(capacity) || capacity <= 0) {
      Alert.alert("Erro", "A capacidade deve ser um número maior que 0.");
      return;
    }
    if (!value || isNaN(value) || value <= 0) {
      Alert.alert("Erro", "O valor deve ser um número maior que 0.");
      return;
    }

    // Lógica de salvar o tanque
    Alert.alert("Sucesso", "Tanque adicionado com sucesso!");
    setName("");
    setCapacity("");
    setValue("");
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

        {/* Campo Valor */}
        <TextInput
          style={styles.input}
          placeholder="Valor (R$)"
          keyboardType="numeric"
          value={value}
          onChangeText={setValue}
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