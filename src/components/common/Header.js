import React from "react";
import { View, Text, Image, StyleSheet } from "react-native";

const Header = ({ children }) => {
  const hasChildren = React.Children.count(children) > 0;

  return (
    <View style={styles.header}>
      <View style={[styles.bar, hasChildren && styles.barWithChildren]}>
        <Image
          source={require("../../assets/icons/icon_bar.png")}
          style={styles.logo}
        />
        <Text style={styles.textLogo}>Aquasense</Text>
      </View>
      {hasChildren && <View style={styles.childrenContainer}>{children}</View>}
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
    padding: 5,
    marginBottom: 15,
  },
  barWithChildren: {
    padding: 10,
    borderBottomWidth: 1,
    borderStyle: "solid",
    borderBottomColor: "#F5F5F5",
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
  childrenContainer: {
    marginTop: 15,
  },
});

export default Header;
