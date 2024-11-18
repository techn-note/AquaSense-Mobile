import React from "react";
import { View, Text, StyleSheet, Image, TouchableOpacity } from "react-native";
import { ProgressBar } from "react-native-paper";
import { MaterialIcons, Entypo } from "@expo/vector-icons";

export default function HomeScreen({ navigation }) {
  return (
    <View style={styles.container}>
      {/* Cabeçalho */}
      <View style={styles.header}>
        <View style={styles.bar}>
          <Image
            source={require("../../assets/icons/icon_bar.png")}
            style={styles.logo}
          />
          <Text style={styles.textLogo}>Aquasense</Text>
        </View>

        <View style={styles.avatar}>
          <Text style={styles.greeting}>Olá, Rodrigo!</Text>
          <TouchableOpacity style={styles.tankSelector}>
            <Text style={styles.tankText}>Tanque 1</Text>
          </TouchableOpacity>
        </View>

        {/* Status Principal */}
        <View style={styles.statusContainer}>
          <MaterialIcons name="thumb-up" size={60} color="white" />
          <Text style={styles.statusText}>Tudo certo por Aqui!</Text>
          <View style={styles.ProgressBarContainer}>
            <ProgressBar
              progress={0.98}
              color="#00DF3A"
              style={styles.progressBar}
            />
            <Text style={styles.progressText}>98%</Text>
          </View>
        </View>
      </View>

      {/* Cartões Informativos */}
      <View style={styles.infoContainer}>
        <Text style={styles.sectionTitle}>Água</Text>
        <View style={styles.card}>
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Oxigenação</Text>
            <Text style={styles.value}>4.3 mg/L</Text>
          </View>
          <View style={styles.separator} />
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Volume</Text>
            <Text style={styles.value}>153 Litros</Text>
          </View>
        </View>

        <Text style={styles.sectionTitle}>Condições Gerais</Text>
        <View style={styles.card}>
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Oxigenação</Text>
            <Text style={styles.value}>4.3 mg/L</Text>
          </View>
          <View style={styles.separator} />
          <View style={styles.dataContainer}>
            <Text style={styles.label}>Volume</Text>
            <Text style={styles.value}>153 Litros</Text>
          </View>
        </View>
      </View>

      {/* Barra de Navegação */}
      <View style={styles.toolBar}>
      <TouchableOpacity style={styles.iconButton}>
        <MaterialIcons name="description" size={24} color="#6C6C6C" />
      </TouchableOpacity>

      <TouchableOpacity style={[styles.iconButton, styles.centerButton]}>
        <MaterialIcons name="home" size={24} color="#FFFFFF" />
      </TouchableOpacity>

      <TouchableOpacity style={styles.iconButton}>
        <MaterialIcons name="tune" size={24} color="#6C6C6C" />
      </TouchableOpacity>
    </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#F9F9F9",
  },
  header: {
    backgroundColor: "#007BFF",
    paddingHorizontal: 20,
    borderBottomLeftRadius: 20,
    borderBottomRightRadius: 20,
    flexDirection: "column",
  },
  bar: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "flex-start",
    borderStyle: "solid",
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: "#F5F5F5",
    marginBottom: 15,
  },
  textLogo: {
    color: "#f5f5f5",
    fontFamily: "Montserrat_SemiBold",
    fontSize: 22,
  },
  logo: {
    width: 50,
    height: 50,
    marginRight: 20,
  },
  greeting: {
    fontSize: 24,
    width: 125,
    fontFamily: "Montserrat_Bold",
    color: "white",
  },
  tankSelector: {
    backgroundColor: "white",
    height: 45,
    paddingVertical: 10,
    paddingHorizontal: 10,
    borderRadius: 10,
    marginTop: 5,
  },
  avatar: {
    flexDirection: "row",
    justifyContent: "space-between",
    paddingRight: 20,
  },
  tankText: {
    fontSize: 16,
    color: "#007BFF",
    fontFamily: "Montserrat_SemiBold",
  },
  statusContainer: {
    alignItems: "center",
    padding: 20,
  },
  statusText: {
    fontSize: 20,
    color: "white",
    fontFamily: "Montserrat_Bold",
    marginTop: 10,
  },
  ProgressBarContainer: {
    marginTop: 15,
    position: "relative",
  },
  progressBar: {
    width: 325,
    height: 36,
    borderRadius: 15,
  },
  progressText: {
    position: "absolute",
    alignSelf: "center",
    fontSize: 24,
    fontFamily: "Montserrat_Bold",
    color: "#fff",
  },
  infoContainer: {
    padding: 20,
  },
  sectionTitle: {
    fontSize: 24,
    color: "#007BFF",
    fontFamily: "Montserrat_SemiBold",
    marginBottom: 10,
  },
  card: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
    width: '100%',
    alignSelf: 'center',
  },
  dataContainer: {
    alignItems: 'center',
  },
  label: {
    color: '#6C6C6C',
    fontSize: 16,
    fontFamily: 'Montserrat_Medium'
  },
  value: {
    color: '#007BFF',
    fontSize: 16,
    fontFamily: 'Montserrat_Bold',
    marginTop: 4,
  },
  separator: {
    width: 3,
    borderRadius: 5,
    height: '60%',
    backgroundColor: '#007BFF',
  },
  toolBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#FFFFFF',
    borderRadius: 25,
    padding: 10,
    width: '65%',
    alignSelf: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  iconButton: {
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  centerButton: {
    backgroundColor: '#007BFF',
    width: 50,
    height: 50,
    borderRadius: 28,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
