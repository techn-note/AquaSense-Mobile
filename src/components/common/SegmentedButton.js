import React, { useState, useRef } from "react";
import {
  View,
  TouchableOpacity,
  StyleSheet,
  Animated,
} from "react-native";
import Icon from 'react-native-vector-icons/MaterialIcons'; // Ícones do Material Icons

export default function SegmentedButtons({ options, selectedOption, onSelect }) {
  const [animValue] = useState(new Animated.Value(0));

  const handlePress = (option) => {
    if (selectedOption !== option) {
      Animated.timing(animValue, {
        toValue: option === "Tanque" ? 0 : 1,
        duration: 200,
        useNativeDriver: false,
      }).start();
    }
    onSelect(option);
  };

  const backgroundColor = animValue.interpolate({
    inputRange: [0, 1],
    outputRange: ["#FFFFFF", "#007BFF"], // Cor de fundo: branco e azul
  });

  const textColor = (option) => {
    return selectedOption === option ? "#FFFFFF" : "#007BFF"; // Azul para não selecionado, branco para selecionado
  };

  const checkMarkPosition = animValue.interpolate({
    inputRange: [0, 1],
    outputRange: [-105, -100], // Animação do ícone de check
  });

  return (
    <View style={styles.container}>
      <Animated.View style={[styles.button]}>
        {options.map((option, index) => (
          <TouchableOpacity
            key={option}
            style={[
              styles.option,
              selectedOption === option && styles.selectedOption,
              index === 0 ,
            ]}
            onPress={() => handlePress(option)}
          >
            <Animated.Text
              style={[
                styles.optionText,
                { color: textColor(option) }, // Cor do texto dinâmica
                selectedOption === option && styles.selectedText,
              ]}
            >
              {option}
            </Animated.Text>
            {selectedOption === option && (
              <Animated.View
                style={[
                  styles.checkMark,
                  { transform: [{ translateX: checkMarkPosition }] },
                ]}
              >
                <Icon name="check" size={24} color="#FFFFFF" /> 
              </Animated.View>
            )}
          </TouchableOpacity>
        ))}
      </Animated.View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    justifyContent: "center",
    alignItems: "center",
    margin: 16,
  },
  button: {
    width: "90%",
    height: 48,
    flexDirection: "row",
    borderRadius: 25,
    overflow: "hidden",
    position: "relative",
    borderWidth: 1,
    borderColor: "#007BFF",
    backgroundColor: "#FFFFFF"
  },
  option: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    height: "100%",
  },
  selectedOption: {
    backgroundColor: "#007BFF", // Cor azul do projeto
  },
  optionText: {
    fontSize: 16,
    fontWeight: "600",
  },
  selectedText: {
    color: "#FFFFFF", // Texto branco quando selecionado
  },
  checkMark: {
    position: "absolute",
    right: 10,
    fontSize: 18,
    color: "#FFFFFF",
  },
});
