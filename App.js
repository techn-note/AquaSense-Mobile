import React, { useCallback } from "react";
import { View, ActivityIndicator } from "react-native";
import { globalStyles } from "./src/styles/globalStyles";
import MainNavigator from "./src/navigation/MainNavigator";
import { useFonts } from "expo-font";
import * as SplashScreen from "expo-splash-screen";
import { AuthProvider } from "./src/context/AuthProvider.js";

export default function App() {
  const [fontsLoaded] = useFonts({
    Montserrat_Bold: require("./src/assets/fonts/Montserrat-Bold.ttf"),
    Montserrat_SemiBold: require("./src/assets/fonts/Montserrat-SemiBold.ttf"),
    Montserrat_Medium: require("./src/assets/fonts/Montserrat-Medium.ttf"),
    Montserrat_Regular: require("./src/assets/fonts/Montserrat-Regular.ttf"),
  });

  SplashScreen.preventAutoHideAsync().catch(() =>
    console.warn("Erro ao prevenir auto-hide da SplashScreen")
  );

  const onLayoutRootView = useCallback(async () => {
    if (fontsLoaded) {
      try {
        await SplashScreen.hideAsync();
      } catch (e) {
        console.warn("Erro ao ocultar a SplashScreen:", e);
      }
    }
  }, [fontsLoaded]);

  if (!fontsLoaded) {
    return null;
  }

  return (
    <View style={globalStyles.container} onLayout={onLayoutRootView}>
      <AuthProvider>
        <MainNavigator />
      </AuthProvider>
    </View>
  );
}
