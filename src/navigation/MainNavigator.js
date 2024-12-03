import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createStackNavigator } from "@react-navigation/stack";
import LoginScreen from "../components/screens/LoginScreen";
import HomeScreen from "../components/screens/HomeScreen";
import RegisterScreen from "../components/screens/RegisterScreen.js";
import DataScreen from "../components/screens/DataScreen.js";
import { AddTankScreen, AddFishScreen } from "../components/screens/SubDataScreen";

const Stack = createStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="LoginScreen"
        screenOptions={{ headerShown: false }}
      >
        <Stack.Screen
          name="LoginScreen"
          component={LoginScreen}
          options={{ title: "Login" }}
        />
        <Stack.Screen
          name="HomeScreen"
          component={HomeScreen}
          options={{ title: "Home" }}
        />
        <Stack.Screen
          name="DataScreen"
          component={DataScreen}
          options={{ title: "Data" }}
        />
        <Stack.Screen
          name="RegisterScreen"
          component={RegisterScreen}
          options={{ title: "Register" }}
        />
        <Stack.Screen
          name="AddFishScreen"
          component={AddFishScreen}
          options={{ title: "AddFish" }}
        />
        <Stack.Screen
          name="AddTankScreen"
          component={AddTankScreen}
          options={{ title: "AddTank" }}
        />
        
      </Stack.Navigator>
    </NavigationContainer>
  );
}
