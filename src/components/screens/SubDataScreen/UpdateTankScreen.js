import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  Alert,
  TouchableOpacity,
  TextInput,
  ScrollView,
} from "react-native";
import { getTanques, getTankByName, updateTank } from "../../../services/api";
import Header from "../../common/Header";
import Toolbar from "../../common/Toolbar";

// Componente para Botões Customizados
const CustomButton = ({ title, onPress }) => (
  <TouchableOpacity style={styles.button} onPress={onPress}>
    <Text style={styles.buttonText}>{title}</Text>
  </TouchableOpacity>
);

const UpdateTankScreen = ({ navigation }) => {
  const [tanques, setTanques] = useState([]); // Lista de tanques
  const [selectedTank, setSelectedTank] = useState(null); // Tanque selecionado
  const [loading, setLoading] = useState(true);

  const [name, setName] = useState("");
  const [capacity, setCapacity] = useState("");
  const [number, setNumber] = useState("");

  // Carregar lista de tanques
  useEffect(() => {
    const fetchTanques = async () => {
      try {
        setLoading(true);
        const response = await getTanques();
        const data = Array.isArray(response) ? response : [];
        setTanques(data);
      } catch (error) {
        Alert.alert("Erro", "Erro ao carregar a lista de tanques.");
      } finally {
        setLoading(false);
      }
    };
  
    fetchTanques();
  }, []);
  

  // Carregar dados do tanque selecionado
  const handleTankSelection = async (name) => {
    try {
      const tankData = await getTankByName(name);
      setSelectedTank(tankData);
      setName(tankData.name);
      setCapacity(String(tankData.capacity));
      setNumber(String(tankData.number));
    } catch (error) {
      Alert.alert("Erro", "Erro ao buscar informações do tanque.");
    }
  };

  // Atualizar tanque
  const handleUpdate = async () => {
    if (!name || !capacity || !number) {
      Alert.alert("Erro", "Todos os campos são obrigatórios.");
      return;
    }
    try {
      const updateData = {
        name,
        capacity: parseFloat(capacity),
        number: parseInt(number, 10),
      };
  
      // Verifique se o selectedTank tem um ID válido
      if (!selectedTank || !selectedTank._id) {
        Alert.alert("Erro", "ID do tanque não encontrado.");
        return;
      }
  
      // Chamada à API para atualizar
      const response = await updateTank(selectedTank._id, updateData);
      if (response?.data?.message) {
        Alert.alert("Sucesso", response.data.message);
        navigation.goBack(); // Redireciona para a tela anterior
      } else {
        Alert.alert("Erro", "Resposta inesperada da API.");
      }
    } catch (error) {
      console.error(error);
      Alert.alert("Erro", "Erro ao atualizar o tanque.");
    }
  };

  if (loading) {
    return (
      <View style={styles.loading}>
        <Text>Carregando...</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Header />

      {/* Lista de Tanques */}
      {!selectedTank ? (
        <View style={styles.form}>
          <Text style={styles.title}>Selecione um Tanque</Text>
          {tanques.map((tank) => (
            <CustomButton
              key={tank.name}
              title={tank.name}
              onPress={() => handleTankSelection(tank.name)}
            />
          ))}
        </View>
      ) : (
        // Formulário de Atualização
        <View style={styles.form}>
          <Text style={styles.title}>Atualizar Tanque</Text>

          <TextInput
            style={styles.input}
            placeholder="Nome"
            maxLength={15}
            value={name}
            onChangeText={setName}
          />

          <TextInput
            style={styles.input}
            placeholder="Capacidade (L)"
            keyboardType="numeric"
            value={capacity}
            onChangeText={setCapacity}
          />

          <TextInput
            style={styles.input}
            placeholder="Número"
            keyboardType="numeric"
            value={number}
            onChangeText={setNumber}
          />

          <TouchableOpacity style={styles.updateButton} onPress={handleUpdate}>
            <Text style={styles.updateButtonText}>Salvar Alterações</Text>
          </TouchableOpacity>
        </View>
      )}

      <Toolbar />
    </ScrollView>
  );
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
    textAlign: "start",
    fontSize: 16,
    color: "#23272A",
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
  updateButton: {
    backgroundColor: "#28a745",
    padding: 15,
    borderRadius: 10,
    alignItems: "center",
  },
  updateButtonText: {
    fontSize: 18,
    color: "#fff",
    fontFamily: "Montserrat_SemiBold",
  },
  loading: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
});

export default UpdateTankScreen;
