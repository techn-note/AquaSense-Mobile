import React, { useState } from "react";
import { StyleSheet, View } from "react-native";
import DropDownPicker from "react-native-dropdown-picker";

function Dropdown({ items, selectedValue, onChange }) {
  const [open, setOpen] = useState(false);

  return (
    <View style={styles.container}>
      <DropDownPicker
        open={open}
        value={selectedValue}  // Aqui, usamos selectedValue diretamente
        items={items}
        setOpen={setOpen}
        setValue={(newValue) => {
          if (onChange) onChange(newValue); // Passa para o onChange do pai
        }}
        style={styles.dropdown}
        textStyle={styles.text}
        dropDownContainerStyle={styles.dropdownContainer}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    height: 45,
    width: 160,
    paddingVertical: 10,
    paddingHorizontal: 15,
    borderRadius: 10,
    marginTop: 5,
    flexDirection: "row",
    alignItems: "center",
    color: "#007BFF",
    borderWidth: 0,
  },
  dropdown: {
    borderWidth: 0,
    backgroundColor: "white",
    borderRadius: 10,
    padding: 10,
    marginTop: 5,
    elevation: 2,
    shadowColor: "#007BFF",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  text: {
    fontSize: 16,
    color: "#007BFF",
    fontFamily: "Montserrat_Medium",
  },
  dropdownContainer: {
    borderWidth: 0,
  },
});

export default Dropdown;
