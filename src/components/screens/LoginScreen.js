import React, { useState, useContext } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert, Image } from "react-native";
import { login } from "../../services/api";
import { AuthContext } from "../../context/AuthProvider";

export default function LoginScreen({ navigation }) {
  const { saveToken } = useContext(AuthContext);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    try {
      const response = await login(email, password);

      if (response?.data?.access_token) {
        const token = response.data.access_token;
        saveToken(token);
        Alert.alert("Sucesso", "Login realizado com sucesso");
        navigation.navigate("HomeScreen");
      } else {
        throw new Error("Token não recebido");
      }
    } catch (error) {
      Alert.alert("Erro", "Credenciais inválidas ou problema de rede.");
      console.error("Erro ao fazer login:", error.message);
    }
  };

  return (
    <View style={styles.container}>
      <Image source={require("../../assets/images/icon.png")} style={styles.logo} />
      <Text style={styles.title}>LOG-IN</Text>

      {/* Campos de entrada */}
      <Text style={styles.label}>E-mail</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite seu e-mail"
        value={email}
        onChangeText={setEmail}
      />
      <Text style={styles.label}>Senha</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite sua senha"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
      />

      {/* Botões */}
      <View style={styles.buttonsContainer}>
        <TouchableOpacity onPress={() => navigation.navigate("RegisterScreen")}>
          <Text style={styles.link}>Criar conta</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={handleLogin}>
          <Text style={styles.buttonText}>Entrar</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: "#fff",
    justifyContent: "center",
    alignItems: "center",
  },
  logo: {
    width: 200, // Aumentado para 200
    height: 200, // Aumentado para 200
    marginBottom: 20,
  },
  title: {
    fontSize: 24,
    fontFamily: "Montserrat_Bold",
    color: "#333",
    marginBottom: 30,
  },
  label: {
    fontSize: 16,
    fontFamily: "Montserrat_Medium",
    color: "#333",
    alignSelf: "flex-start",
    marginLeft: 25,
  },
  input: {
    width: "90%",
    height: 40,
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 8,
    paddingHorizontal: 10,
    fontFamily: "Montserrat_Regular",
    marginBottom: 20,
  },
  buttonsContainer: {
    flexDirection: "row",
    gap: 75
  },
  button: {
    backgroundColor: "#007BFF",
    width: 125,
    height: 40,
    borderRadius: 8,
    justifyContent: "center",
    alignItems: "center",
  },
  buttonText: {
    color: "#fff",
    fontSize: 16,
    fontFamily: "Montserrat_Bold",
  },
  errorText: {
    color: "red",
    fontFamily: "Montserrat_Medium",
    fontSize: 14,
    marginTop: 10,
  },
  link: {
    color: "#007BFF",
    fontFamily: "Montserrat_Medium",
    fontSize: 16,
  },
});
