import React, { useState } from "react";
import { StyleSheet, View } from "react-native";
import DropDownPicker from "react-native-dropdown-picker";

function Dropdown({ items, selectedValue, onChange, background = false }) {
  const [open, setOpen] = useState(false);
  const [value, setValue] = useState(selectedValue);
  // Atualiza valor local e propaga para o pai
  const handleChange = (val) => {
    setValue(val);
    if (onChange) onChange(val);
  };

  // Sincroniza valor local com prop externo
  React.useEffect(() => {
    setValue(selectedValue);
  }, [selectedValue]);

  // Estilos dinâmicos
  const dynamicDropdownStyle = [
    styles.dropdown,
    background && { backgroundColor: "#007BFF" },
  ];
  const dynamicTextStyle = [styles.text, background && { color: "#fff" }];

  return (
    <View style={styles.container}>
      <DropDownPicker
        open={open}
        value={value}
        items={items}
        setOpen={setOpen}
        setValue={handleChange}
        style={dynamicDropdownStyle}
        textStyle={dynamicTextStyle}
        dropDownContainerStyle={styles.dropdownContainer}
        listMode="SCROLLVIEW"
        zIndex={1000}
        zIndexInverse={1000}
        placeholder="Selecione"
        showArrowIcon={true}
        showTickIcon={true}
        autoScroll={true}
        closeAfterSelecting={true}
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
