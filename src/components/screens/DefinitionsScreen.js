import React, { useState, useEffect, useContext } from "react";
import {
  View,
  Text,
  StyleSheet,
  Image,
  TouchableOpacity,
  Alert,
} from "react-native";
import { useNavigation } from "@react-navigation/native"; // Para navegação
import { AuthContext } from "../../context/AuthProvider"; // Importando o AuthContext
import { getUserName } from "../../services/api";
import Header from "../common/Header";
import Toolbar from "../common/Toolbar";
import { getToken } from "../../utils/Auth";

const DefinitionsScreen = () => {
  const [userName, setUserName] = useState("Usuário");
  const [userImage, setUserImage] = useState(""); // Caminho para a imagem do usuário, se necessário.
  const { removeUserToken } = useContext(AuthContext); // Usando o contexto de autenticação
  const navigation = useNavigation(); // Hook para navegação

  useEffect(() => {
    fetchUserName();
  }, []);

  const fetchUserName = async () => {
    try {
      const token = await getToken();
      if (token) {
        const name = await getUserName(token);
        setUserName(name);
      } else {
        setUserName("Usuário não autenticado");
      }
    } catch (error) {
      console.error("Erro ao buscar nome do usuário:", error);
      setUserName("Usuário");
    }
  };

  const logout = async () => {
    try {
      await removeUserToken(); // Remove o token e redefine o estado
      Alert.alert("Sessão encerrada", "Você será redirecionado para a tela de login.");
      navigation.replace("LoginScreen"); // Redireciona para a tela de login
    } catch (error) {
      console.error("Erro ao encerrar a sessão:", error);
      Alert.alert("Erro", "Não foi possível encerrar a sessão.");
    }
  };

  return (
    <>
      <Header />
      <View style={styles.container}>
        <Image
          source={
            userImage
              ? { uri: userImage }
              : require("../../assets/images/avatar.png")
          }
          style={styles.profileImage}
        />

        <Text style={styles.userName}>{userName}</Text>

        <TouchableOpacity
          style={styles.button}
          onPress={() => Alert.alert("Editar dados pessoais")}
        >
          <Text style={styles.buttonText}>Editar dados Pessoais</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.backButton} onPress={logout}>
          <Text style={styles.backButtonText}>Sair da Sessão</Text>
          <Image source={require("../../assets/icons/icon_sign_in.png")} />
        </TouchableOpacity>
        <Toolbar />
      </View>
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#f5f5f5",
    alignItems: "center",
    padding: 20,
  },
  profileImage: {
    width: 120,
    height: 120,
    borderRadius: 60, // Fazendo a imagem ficar circular
    marginBottom: 20,
    borderWidth: 2,
    borderColor: "#007BFF",
  },
  userName: {
    fontSize: 24,
    fontFamily: "Montserrat_Bold",
    color: "#23272A",
    marginBottom: 20,
  },
  button: {
    backgroundColor: "#FFFFFF",
    padding: 16,
    borderRadius: 25,
    marginVertical: 8,
    elevation: 2,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
    width: "100%",
  },
  buttonText: {
    textAlign: "start",
    fontSize: 16,
    color: "#23272A",
  },
  backButton: {
    backgroundColor: "#ff5c5c",
    padding: 8,
    paddingHorizontal: 20,
    borderRadius: 25,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginVertical: 8,
    elevation: 2,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
    width: "100%",
    marginTop: 20,
  },
  backButtonText: {
    fontSize: 16,
    color: "#fff",
    fontFamily: "Montserrat_SemiBold",
  },
});

export default DefinitionsScreen;
