import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createStackNavigator } from "@react-navigation/stack";
import LoginScreen from "../components/screens/LoginScreen";
import HomeScreen from "../components/screens/HomeScreen";
import RegisterScreen from "../components/screens/RegisterScreen.js";
import DataScreen from "../components/screens/DataScreen.js";
import AddTankScreen from "../components/screens/SubDataScreen/AddTankScreen.js";
import AddFishScreen from "../components/screens/SubDataScreen/AddFishScreen.js";
import UpdateFishScreen from "../components/screens/SubDataScreen/UpdateFishScreen.js";
import UpdateTankScreen from "../components/screens/SubDataScreen/UpdateTankScreen.js";
import GraphScreen from "../components/screens/SubDataScreen/GraphScreen.js";
import DefinitionsScreen from "../components/screens/DefinitionsScreen.js";

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
        <Stack.Screen
          name="UpdateFishScreen"
          component={UpdateFishScreen}
          options={{ title: "UpdateFish" }}
        />
        <Stack.Screen
          name="UpdateTankScreen"
          component={UpdateTankScreen}
          options={{ title: "UpdateTank" }}
        />
        <Stack.Screen
          name="GraphScreen"
          component={GraphScreen}
          options={{ title: "Graph" }}
        />
        <Stack.Screen
          name="DefinitionsScreen"
          component={DefinitionsScreen}
          options={{ title: "Definitions" }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
