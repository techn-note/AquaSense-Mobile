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

function AddFishScreen() {
  const [name, setName] = useState("");
  const [age, setAge] = useState("");
  const [species, setSpecies] = useState("");
  const [weight, setWeight] = useState("");
  const [quantity, setQuantity] = useState("");

  const handleSave = () => {
    // Validações básicas
    if (!name || name.length === 0) {
      Alert.alert("Erro", "O nome é obrigatório.");
      return;
    }
    if (!age || isNaN(age) || age <= 0) {
      Alert.alert("Erro", "A idade deve ser um número positivo.");
      return;
    }
    if (!species || species.length === 0) {
      Alert.alert("Erro", "A espécie é obrigatória.");
      return;
    }
    if (!weight || isNaN(weight) || weight <= 0) {
      Alert.alert("Erro", "O peso deve ser um número positivo.");
      return;
    }
    if (!quantity || isNaN(quantity) || quantity <= 0) {
      Alert.alert("Erro", "A quantidade deve ser um número positivo.");
      return;
    }

    // Lógica de salvar o peixe
    Alert.alert("Sucesso", "Peixe adicionado com sucesso!");
    setName("");
    setAge("");
    setSpecies("");
    setWeight("");
    setQuantity("");
  };

  return (
    <View style={styles.container}>
      <Header />
      <Text style={styles.title}>Adicionar Peixe</Text>

      {/* Campo Nome */}
      <TextInput
        style={styles.input}
        placeholder="Nome do peixe"
        value={name}
        onChangeText={setName}
      />

      {/* Campo Idade */}
      <TextInput
        style={styles.input}
        placeholder="Idade (em dias)"
        keyboardType="numeric"
        value={age}
        onChangeText={setAge}
      />

      {/* Campo Espécie */}
      <TextInput
        style={styles.input}
        placeholder="Espécie"
        value={species}
        onChangeText={setSpecies}
      />

      {/* Campo Peso */}
      <TextInput
        style={styles.input}
        placeholder="Peso (em gramas)"
        keyboardType="numeric"
        value={weight}
        onChangeText={setWeight}
      />

      {/* Campo Quantidade */}
      <TextInput
        style={styles.input}
        placeholder="Quantidade"
        keyboardType="numeric"
        value={quantity}
        onChangeText={setQuantity}
      />

      {/* Botão de Salvar */}
      <TouchableOpacity style={styles.saveButton} onPress={handleSave}>
        <Text style={styles.saveButtonText}>Salvar</Text>
      </TouchableOpacity>
      <Toolbar />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#f5f5f5",
    padding: 20,
    marginTop: 20,
  },
  title: {
    fontSize: 24,
    fontFamily: "Montserrat_Bold",
    color: "#007BFF",
    marginBottom: 20,
    textAlign: "center",
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

export default AddFishScreen;
