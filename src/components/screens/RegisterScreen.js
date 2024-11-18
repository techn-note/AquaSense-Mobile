import React from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Image,
} from "react-native";

export default function SignInScreen({ navigation }) {
  const handleSignIn = () => {
    // Lógica futura para autenticação via API
    // Exemplo: const response = await signInService(email, password);
    navigation.navigate("HomeScreen"); // Redireciona para a HomeScreen
  };

  return (
    <View style={styles.container}>
      {/* Logo */}
      <Text style={styles.title}>SIGN-IN</Text>

      {/* Campos de entrada */}
      <Text style={styles.label}>Nome</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite seu nome"
        placeholderTextColor="#999"
      />
      <Text style={styles.label}>E-mail</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite seu e-mail"
        placeholderTextColor="#999"
        keyboardType="email-address"
      />
      <Text style={styles.label}>Estado</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite seu estado"
        placeholderTextColor="#999"
      />
      <Text style={styles.label}>Idade</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite sua idade"
        placeholderTextColor="#999"
        keyboardType="numeric"
      />
      <Text style={styles.label}>Cidade</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite sua cidade"
        placeholderTextColor="#999"
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
        <TouchableOpacity onPress={() => navigation.navigate("LoginScreen")}>
          <Text style={styles.link}>Logar</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={handleSignIn}>
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
});
