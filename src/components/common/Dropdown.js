import React, { useState } from 'react';
import { StyleSheet, View } from 'react-native';
import DropDownPicker from 'react-native-dropdown-picker';

const Dropdown = () => {
  const [open, setOpen] = useState(false);
  const [value, setValue] = useState('tanque1');
  const [items, setItems] = useState([
    { label: 'Tanque 1', value: 'tanque1' },
    { label: 'Tanque 2', value: 'tanque2' },
  ]);

  const handleValueChange = (value) => {
    setValue(value);
    if (onChange) {
      onChange(value);
    }
  };
  

  return (
    <View style={styles.container}>
      <DropDownPicker
        open={open}
        value={value}
        items={items}
        setOpen={setOpen}
        setValue={handleValueChange}
        setItems={setItems}
        style={styles.dropdown}
        textStyle={styles.text}
        dropDownContainerStyle={styles.dropdownContainer}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    height: 45,
    width: 160,
    paddingVertical: 10,
    paddingHorizontal: 15,
    borderRadius: 10,
    marginTop: 5,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    color:"#007BFF",
    borderWidth: 0
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
    borderWidth: 0
  },
});

export default Dropdown;
