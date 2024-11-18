import React from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Image,
} from "react-native";

export default function LoginScreen({ navigation }) {
  const handleLogin = () => {
    // Lógica futura para autenticação via API
    // Exemplo: const response = await loginService(email, password);
    navigation.navigate("HomeScreen"); // Redireciona para a HomeScreen
  };

  return (
    <View style={styles.container}>
      {/* Logo */}
      <Image
        source={require("../../assets/images/icon.png")}
        style={styles.logo}
      />
      <Text style={styles.title}>LOG-IN</Text>

      {/* Campos de entrada */}
      <Text style={styles.label}>E-mail</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite seu e-mail"
        placeholderTextColor="#999"
        keyboardType="email-address"
      />
      <Text style={styles.label}>Senha</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite sua senha"
        placeholderTextColor="#999"
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
  buttons: {
    flexDirection: "row",
    gap: 85,
    marginTop: 20,
  },
  linkButton: {
    justifyContent: "center",
    alignItems: "center",
  },
  link: {
    color: "#007BFF",
    fontFamily: "Montserrat_Medium",
    fontSize: 16,
  },
  buttonsContainer: {
    flexDirection: 'row',
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
});
