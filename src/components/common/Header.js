import React from "react";
import { View, Text, Image, StyleSheet } from "react-native";

const Header = ({ children }) => {
  return (
    <View style={styles.header}>
      <View style={styles.bar}>
        <Image
          source={require("../../assets/icons/icon_bar.png")}
          style={styles.logo}
        />
        <Text style={styles.textLogo}>Aquasense</Text>
      </View>
      {children && <View style={styles.childrenContainer}>{children}</View>}
    </View>
  );
};

const styles = StyleSheet.create({
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

});

export default Header;
