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
import { getPeixes, getPeixeByName, updatePeixe } from "../../../services/api";
import Header from "../../common/Header";
import Toolbar from "../../common/Toolbar";

// Componente para Botões Customizados
const CustomButton = ({ title, onPress }) => (
  <TouchableOpacity style={styles.button} onPress={onPress}>
    <Text style={styles.buttonText}>{title}</Text>
  </TouchableOpacity>
);

const UpdateFishScreen = ({ navigation }) => {
  const [peixes, setPeixes] = useState([]); // Lista de peixes
  const [selectedFish, setSelectedFish] = useState(null); // Peixe selecionado
  const [loading, setLoading] = useState(true);

  const [name, setName] = useState("");
  const [age, setAge] = useState("");
  const [species, setSpecies] = useState("");
  const [weight, setWeight] = useState("");
  const [quantity, setQuantity] = useState("");

  // Carregar lista de peixes
  useEffect(() => {
    async function fetchPeixes() {
      try {
        setLoading(true);
        const response = await getPeixes();
        const fishArray = response.data.data[0];
        console.log("Dados carregados:", fishArray); // Verifique os dados aqui
        setPeixes(fishArray);
      } catch (error) {
        Alert.alert("Erro", "Erro ao carregar a lista de peixes.");
      } finally {
        setLoading(false);
      }
    }
  
    fetchPeixes();
  }, []);
  

  // Carregar dados do peixe selecionado
//   Pesquisar por ID

  const handleFishSelection = async (name) => {
    try {
      const fishData = await getPeixeByName(name);
      console.log(`Fazendo teste da api/peixes/name/${name}`)
      setSelectedFish(fishData);
      setName(fishData.nome);
      setAge(String(fishData.idade));
      setSpecies(fishData.especie);
      setWeight(String(fishData.peso));
      setQuantity(String(fishData.quantidade));
    } catch (error) {
      Alert.alert("Erro", "Erro ao buscar informações do peixe.");
    }
  };

  // Atualizar peixe
const handleUpdate = async () => {
  if (!name || !age || !species || !weight || !quantity) {
    Alert.alert("Erro", "Todos os campos são obrigatórios.");
    return;
  }
  
  try {
    const updateData = {
      nome: name,
      idade: parseInt(age, 10),
      especie: species,  // Corrigido de 'especies' para 'especie'
      peso: parseFloat(weight),
      quantidade: parseInt(quantity, 10),
    };

    // Confirmação do ID e dados enviados
    console.log("Atualizando peixe:", selectedFish._id, updateData);

    // Chamada para atualizar o peixe
    const updatedFish = await updatePeixe(selectedFish._id, updateData);

    // Confirmação da resposta da API
    console.log("Resposta da atualização:", updatedFish);

    // Exibir alerta de sucesso
    Alert.alert("Sucesso", "Peixe atualizado com sucesso!");

    // Redirecionar para a tela Home
    navigation.goBack();  // Redireciona para a tela anterior, geralmente a HomeScreen

  } catch (error) {
    Alert.alert("Erro", "Erro ao atualizar o peixe.");
    console.error("Erro ao atualizar peixe:", error.response?.data || error.message);
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

      {/* Lista de Peixes */}
      {!selectedFish ? (
        <View style={styles.form}>
          <Text style={styles.title}>Selecione um Peixe</Text>
          {peixes.map((fish) => (
            <CustomButton
              key={fish.id} 
              title={fish.nome}
              onPress={() => handleFishSelection(fish.nome)}
            />
          ))}
        </View>
      ) : (
        // Formulário de Atualização
        <View style={styles.form}>
          <Text style={styles.title}>Atualizar Peixe</Text>

          <TextInput
            style={styles.input}
            placeholder="Nome"
            maxLength={15}
            value={name}
            onChangeText={setName}
          />

          <TextInput
            style={styles.input}
            placeholder="Idade (dias)"
            keyboardType="numeric"
            value={age}
            onChangeText={setAge}
          />

          <TextInput
            style={styles.input}
            placeholder="Espécie"
            value={species}
            onChangeText={setSpecies}
          />

          <TextInput
            style={styles.input}
            placeholder="Peso (Kg)"
            keyboardType="numeric"
            value={weight}
            onChangeText={setWeight}
          />

          <TextInput
            style={styles.input}
            placeholder="Quantidade"
            keyboardType="numeric"
            value={quantity}
            onChangeText={setQuantity}
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

export default UpdateFishScreen;
