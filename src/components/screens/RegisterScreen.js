import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
} from "react-native";
import { register } from "../../services/api";

export default function SignInScreen({ navigation }) {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [age, setAge] = useState("");
  const [country, setCountry] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleRegister = async () => {
    try {
      const userData = { name, email, age, country, password };
      const response = await register(userData);

      Alert.alert("Sucesso", "Usuário cadastrado com sucesso!");
      
      navigation.navigate("LoginScreen");
    } catch (error) {
      console.error("Erro ao cadastrar:", error.message);
      setError(error?.response?.data?.message || "Erro ao cadastrar usuário.");
      Alert.alert("Erro", error?.response?.data?.message || "Erro ao cadastrar usuário.");
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>SIGN-IN</Text>

      {/* Campos de entrada */}
      <Text style={styles.label}>Nome</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite seu nome"
        value={name}
        onChangeText={setName}
      />
      <Text style={styles.label}>E-mail</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite seu e-mail"
        value={email}
        onChangeText={setEmail}
        keyboardType="email-address"
      />
      <Text style={styles.label}>Estado</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite seu estado"
        value={country}
        onChangeText={setCountry}
      />
      <Text style={styles.label}>Idade</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite sua idade"
        value={age}
        onChangeText={setAge}
        keyboardType="numeric"
      />
      <Text style={styles.label}>Senha</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite sua senha"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
      />

      {/* Exibe a mensagem de erro se houver */}
      {error ? <Text style={styles.errorText}>{error}</Text> : null}

      {/* Botões */}
      <View style={styles.buttonsContainer}>
        <TouchableOpacity onPress={() => navigation.navigate("LoginScreen")}>
          <Text style={styles.link}>Logar</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={handleRegister}>
          <Text style={styles.buttonText}>Cadastrar</Text>
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
    flexDirection: 'row',
    gap: 75,
    marginTop: 20,
  },
  link: {
    color: "#007BFF",
    fontFamily: "Montserrat_Medium",
    fontSize: 16,
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
});
