//
//  WeatherApp.swift
//  Weather
//
//  Created by NTLG1 on 29/01/2024.
//

import SwiftUI

struct Location {
    let name: String
    let latitude: Double
    let longitude: Double
}

@main
struct WeatherApp: App {
    
    @State private var isDataFetched = false
    @State private var shouldRefresh = true
    @State private var isSettingsDialogPresented = false
    @State private var fetchError: Error?
    @State private var apiKey: String = UserDefaults.standard.string(forKey: "apiKey") ?? ""
    @State private var latitude1: String = UserDefaults.standard.string(forKey: "latitude1") ?? "21.0278"
    @State private var longitude1: String = UserDefaults.standard.string(forKey: "longitude1") ?? "105.8342"
    @State private var latitude2: String = UserDefaults.standard.string(forKey: "latitude2") ?? "48.8566"
    @State private var longitude2: String = UserDefaults.standard.string(forKey: "longitude2") ?? "2.3522"
    @State private var latitude3: String = UserDefaults.standard.string(forKey: "latitude3") ?? "43.6047"
    @State private var longitude3: String = UserDefaults.standard.string(forKey: "longitude3") ?? "1.4442"
    @StateObject var viewModel = WeatherViewModel(apiKey: UserDefaults.standard.string(forKey: "apiKey") ?? "", locations: [
        Location(name: "Location 1", latitude: Double(UserDefaults.standard.string(forKey: "latitude1") ?? "21.0278") ?? 21.0278, longitude: Double(UserDefaults.standard.string(forKey: "longitude1") ?? "105.8342") ?? 105.8342),
        Location(name: "Location 2", latitude: Double(UserDefaults.standard.string(forKey: "latitude2") ?? "48.8566") ?? 48.8566, longitude: Double(UserDefaults.standard.string(forKey: "longitude2") ?? "2.3522") ?? 2.3522),
        Location(name: "Location 3", latitude: Double(UserDefaults.standard.string(forKey: "latitude3") ?? "43.6047") ?? 43.6047, longitude: Double(UserDefaults.standard.string(forKey: "longitude3") ?? "1.4442") ?? 1.4442)
    ])
    
    var body: some Scene {
        WindowGroup {
            if isDataFetched, shouldRefresh == false {
                MainView(shouldRefresh: $shouldRefresh, isSettingsDialogPresented: $isSettingsDialogPresented)
                    .environmentObject(viewModel)
                    .sheet(isPresented: $isSettingsDialogPresented, content: {
                        SettingsDialog(shouldRefresh: $shouldRefresh, isPresented: $isSettingsDialogPresented, apiKey: $apiKey, latitude1: $latitude1, longitude1: $longitude1, latitude2: $latitude2, longitude2: $longitude2, latitude3: $latitude3, longitude3: $longitude3)
                    })
            } else {
                VStack {
                    ProgressView()
                    Text("Loading weather data")
                        .foregroundColor(.gray)
                        .padding()
                    Text("Settings")
                        .foregroundColor(.blue)
                        .onTapGesture {
                            isSettingsDialogPresented = true
                        }                }
                // Show a loading indicator while fetching data
                .onAppear {
                    Task {
                        await fetchWeatherAndForecastData()
                    }
                }
                .sheet(isPresented: $isSettingsDialogPresented, content: {
                    SettingsDialog(shouldRefresh: $shouldRefresh, isPresented: $isSettingsDialogPresented, apiKey: $apiKey, latitude1: $latitude1, longitude1: $longitude1, latitude2: $latitude2, longitude2: $longitude2, latitude3: $latitude3, longitude3: $longitude3)
                })
                .alert("No connection or invalid API key", isPresented: .constant(fetchError != nil)) {
                    Button("Settings") {
                        isSettingsDialogPresented = true
                    }
                    Button("OK") {}
                }
                
            }
            
        }
    }
    
    func fetchWeatherAndForecastData() async {
              while shouldRefresh {
                  viewModel.updateLocations(withNewLocations: [
                      Location(name: "Location 1", latitude: Double(latitude1) ?? 21.0278, longitude: Double(longitude1) ?? 105.8342),
                      Location(name: "Location 2", latitude: Double(latitude2) ?? 48.8566, longitude: Double(longitude2) ?? 2.3522),
                      Location(name: "Location 3", latitude: Double(latitude3) ?? 43.6047, longitude: Double(longitude3) ?? 1.4442)
                  ])
                  viewModel.updateAPIKey(withNewAPIKey: apiKey)
                  do {
                      // Clear existing data before fetching new data
                      viewModel.weatherData.removeAll()
                      viewModel.forecastData.removeAll()
                      
                      for location in viewModel.locations {
                          let weather = try await viewModel.weatherApiClient.getWeatherData(lat: location.latitude, lon: location.longitude, apiKey: viewModel.apiKey)
                          let forecast = try await viewModel.forecastApiClient.getForecastData(lat: location.latitude, lon: location.longitude, apiKey: viewModel.apiKey)

                          viewModel.weatherData.append(weather)
                          viewModel.forecastData.append(forecast)
                      }
                      isDataFetched = true // Mark data as fetched when successful
                      fetchError = nil // Reset error state
                      shouldRefresh = false
                  } catch {
                      print("Error fetching weather data: \(error)")
                      fetchError = error // Store the error for displaying in the alert
                      // Delay before retrying to avoid continuous retrying too fast
                      await Task.sleep(2 * 1_000_000_000) // 2 seconds delay
                  }
              }
          }
      }

class WeatherViewModel: ObservableObject {
    @Published var weatherData: [ResponseBody] = []
    @Published var forecastData: [ForecastResponseBody] = []
    let weatherApiClient = WeatherApiClient()
    let forecastApiClient = ForecastApiClient()
    @Published var locations: [Location]
    @Published var apiKey: String
    
    init(apiKey: String, locations: [Location]) {
        self.apiKey = apiKey
        self.locations = locations
    }
    
    func updateLocations(withNewLocations newLocations: [Location]) {
        locations = newLocations
    }
    
    func updateAPIKey(withNewAPIKey newAPIKey: String) {
        apiKey = newAPIKey
    }
}

struct SettingsDialog: View {
    @Binding var shouldRefresh: Bool
    @Binding var isPresented: Bool
    @Binding var apiKey: String
    @Binding var latitude1: String
    @Binding var longitude1: String
    @Binding var latitude2: String
    @Binding var longitude2: String
    @Binding var latitude3: String
    @Binding var longitude3: String
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("API Key")) {
                    TextField("API Key", text: $apiKey)
                }
                
                Section(header: Text("City 1")) {
                    TextField("Latitude", text: $latitude1)
                    TextField("Longitude", text: $longitude1)
                }
                
                Section(header: Text("City 2")) {
                    TextField("Latitude", text: $latitude2)
                    TextField("Longitude", text: $longitude2)
                }
                
                Section(header: Text("City 3")) {
                    TextField("Latitude", text: $latitude3)
                    TextField("Longitude", text: $longitude3)
                }
            }
            .navigationTitle("Settings")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        saveSettings()
                        shouldRefresh = true
                        isPresented = false
                    }
                }
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        isPresented = false
                    }
                }
            }
        }
    }
    
    private func saveSettings() {
        UserDefaults.standard.set(apiKey, forKey: "apiKey")
        UserDefaults.standard.set(latitude1, forKey: "latitude1")
        UserDefaults.standard.set(longitude1, forKey: "longitude1")
        UserDefaults.standard.set(latitude2, forKey: "latitude2")
        UserDefaults.standard.set(longitude2, forKey: "longitude2")
        UserDefaults.standard.set(latitude3, forKey: "latitude3")
        UserDefaults.standard.set(longitude3, forKey: "longitude3")
    }
}

