//
//  MainView.swift
//  Weather
//
//  Created by NTLG1 on 29/01/2024.
//

import SwiftUI

struct MainView: View {
    
    @EnvironmentObject var viewModel: WeatherViewModel
    @State private var selectedTab: Tabs = .FirstTab
    @Binding var shouldRefresh: Bool
    @Binding var isSettingsDialogPresented: Bool
    private let swipeThreshold: CGFloat = 75

    var body: some View {
        VStack(spacing: 0) {
            // Tab bar
            HStack {
                // Refresh button
                Button(action: {
                    // Action for refresh button
                    shouldRefresh = true
                }) {
                    Image(systemName: "arrow.clockwise.circle.fill")
                        .font(.title)
                        .foregroundColor(.blue)
                        .padding(.horizontal)
                }
                
                Spacer()
                // Weather title
                Text("Weather")
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(.white)

                Spacer()
                
                // Settings button
                Button(action: {
                    // Action for settings button.
                    isSettingsDialogPresented = true
                }) {
                    Image(systemName: "gear")
                        .font(.title)
                        .foregroundColor(.blue)
                        .padding(.horizontal)
                }
            }
            .padding(.bottom)
            .background(Color.black)
    
            
            if let weatherData1 = viewModel.weatherData[safe: 0], let weatherData2 = viewModel.weatherData[safe: 1], let weatherData3 = viewModel.weatherData[safe: 2] {
                // City Tab
                HStack {
                    Spacer()
                    
                    // First Tab
                    ZStack {
                        Text("\(weatherData1.name)")
                            .font(.system(size: 14))
                            .multilineTextAlignment(.center)
                            .foregroundColor(selectedTab == .FirstTab ? Color.blue : Color.white)
                    }
                    .frame(width: 100)
                    .onTapGesture {
                        self.selectedTab = .FirstTab
                    }
                    
                    Spacer()
                    
                    // Second Tab
                    ZStack {
                        Text("\(weatherData2.name)")
                            .font(.system(size: 14))
                            .multilineTextAlignment(.center)
                            .foregroundColor(selectedTab == .SecondTab ? Color.blue : Color.white)
                    }
                    .frame(width: 100)
                    .onTapGesture {
                        self.selectedTab = .SecondTab
                    }
                    
                    Spacer()
                    
                    // Third Tab
                    ZStack {
                        Text("\(weatherData3.name)")
                            .font(.system(size: 14))
                            .multilineTextAlignment(.center)
                            .foregroundColor(selectedTab == .ThirdTab ? Color.blue : Color.white)
                    }
                    .frame(width: 100)
                    .onTapGesture {
                        self.selectedTab = .ThirdTab
                    }
                    
                    Spacer()
                }
                .padding(.bottom)
                .background(Color.black.edgesIgnoringSafeArea(.all))
                .gesture(
                    DragGesture()
                        .onEnded { gesture in
                            if gesture.translation.width > swipeThreshold {
                                if self.selectedTab != .FirstTab {
                                    self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue - 1)!
                                }
                            } else if gesture.translation.width < -swipeThreshold{
                                if self.selectedTab != .ThirdTab {
                                    self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue + 1)!
                                }
                            }
                        }
                )
            }
            
//            // Content based on selected tab
//            TabView(selection: $selectedTab) {
//                FirstTabView()
//                    .tag(Tabs.FirstTab)
//                SecondTabView()
//                    .tag(Tabs.SecondTab)
//                ThirdTabView()
//                    .tag(Tabs.ThirdTab)
//            }
//            .gesture(
//                DragGesture()
//                    .onEnded { gesture in
//                        if gesture.translation.width > swipeThreshold {
//                            if self.selectedTab != .FirstTab {
//                                self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue - 1)!
//                            }
//                        } else if gesture.translation.width < -swipeThreshold {
//                            if self.selectedTab != .ThirdTab {
//                                self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue + 1)!
//                            }
//                        }
//                    }
//            )

            // Content based on selected tab
            switch selectedTab {
            case .FirstTab:
                FirstTabView()
                .gesture(
                    DragGesture()
                        .onEnded { gesture in
                            if gesture.translation.width > swipeThreshold {
                                if self.selectedTab != .FirstTab {
                                    self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue - 1)!
                                }
                            } else if gesture.translation.width < -swipeThreshold {
                                if self.selectedTab != .ThirdTab {
                                    self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue + 1)!
                                }
                            }
                        }
                )
            case .SecondTab:
                SecondTabView()
                .gesture(
                    DragGesture()
                        .onEnded { gesture in
                            if gesture.translation.width > swipeThreshold {
                                if self.selectedTab != .FirstTab {
                                    self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue - 1)!
                                }
                            } else if gesture.translation.width < -swipeThreshold {
                                if self.selectedTab != .ThirdTab {
                                    self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue + 1)!
                                }
                            }
                        }
                )
            case .ThirdTab:
                ThirdTabView()
                .gesture(
                    DragGesture()
                        .onEnded { gesture in
                            if gesture.translation.width > swipeThreshold {
                                if self.selectedTab != .FirstTab {
                                    self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue - 1)!
                                }
                            } else if gesture.translation.width < -swipeThreshold {
                                if self.selectedTab != .ThirdTab {
                                    self.selectedTab = Tabs(rawValue: self.selectedTab.rawValue + 1)!
                                }
                            }
                        }
                )
            }
            
        }
    }
}
struct FirstTabView : View {
    var body : some View {
        contentTabView(locationIndex: 0)
    }
}

struct SecondTabView : View {
    var body : some View {
        contentTabView(locationIndex: 1)
    }
}

struct ThirdTabView : View {
    var body : some View {
        contentTabView(locationIndex: 2)
    }
}

enum Tabs: Int {
    case FirstTab = 0
    case SecondTab = 1
    case ThirdTab = 2
}

struct contentTabView: View {
    @EnvironmentObject var viewModel: WeatherViewModel
    let locationIndex: Int
    
    var body: some View {
        ZStack {
//            LinearGradient(gradient: Gradient(colors: [.blue, .white]), startPoint: .topLeading, endPoint: .bottomTrailing)
//                .edgesIgnoringSafeArea(.all)
            LinearGradient(
                gradient: Gradient(colors: [Color(red: 100/255, green: 149/255, blue: 237/255), Color(red: 135/255, green: 206/255, blue: 250/255)]),
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .edgesIgnoringSafeArea(.all)
            
            VStack {
                WeatherView(locationIndex: locationIndex)
                ScrollView {
                    VStack {
                        if let forecastData = viewModel.forecastData[safe: locationIndex] { // Check if forecast data exists
                            ForEach(0..<forecastData.list.count, id: \.self) { index in
                                ForecastRow(forecast: forecastData.list[index], locationIndex: locationIndex)
                            }} else {
                               Text("No forecast data available")
                                   .foregroundColor(.white)
                           }
                    }
                }
            }
        }
    }
}

struct WeatherView: View {

    @EnvironmentObject var viewModel: WeatherViewModel
    let locationIndex: Int

    var body: some View {
        if let weatherData = viewModel.weatherData[safe: locationIndex] {
            ZStack {
                // Background frame
                let edgeInsets = EdgeInsets(top: 73, leading: 72, bottom: 199, trailing: 113)
                Image("frame")
                    .resizable(capInsets: edgeInsets, resizingMode: .stretch)
                    .frame(height: 310)
                // Content of the view
                VStack{
                    HStack {
                        Text("\(capitalizeFirstLetterEachWord(weatherData.weather[0].description))")
                            .foregroundColor(.white)
                            .font(.system(size: 14))
                        
                        Spacer()
                        
                        Text("\(weatherData.name)")
                            .foregroundColor(.white)
                            .font(.system(size: 14))
                    }
                    .padding(.horizontal, 40)
                    
                    // Weather icon ImageView
                    Image("\(getWeatherIconResource(weatherData.weather[0].main))")
                        .resizable()
                        .frame(width: 150, height: 150)
                    
                    
                    HStack {
                        Text("💦 Humidity: \(String(format: "%.0f %%", weatherData.main.humidity))\n💨 Wind: \(String(format: "%.1f km/h", (weatherData.wind.speed) * 3.6))\n🕛 Pressure: \(String(format: "%.0f hPa", weatherData.main.pressure))")
                            .foregroundColor(.white)
                            .font(.system(size: 14))
                        
                        Spacer()
                        
                        Text("🌡 \(String(format: "%.1f°C",kelvinToCelsius(weatherData.main.temp)))\nMin: \(String(format: "%.1f°C",kelvinToCelsius(weatherData.main.temp_min)))\nMax: \(String(format: "%.1f°C",kelvinToCelsius(weatherData.main.temp_max)))")
                            .foregroundColor(.white)
                            .font(.system(size: 14))
                    }
                    .padding(.horizontal, 40)
                }
            }
        }
    }
}

struct ForecastRow: View {
    var forecast: ForecastResponseBody.Forecast
    let locationIndex: Int

    var body: some View {
        HStack {
            Spacer()
            VStack(alignment: .center, spacing: 8) {
                Text("\(formatDate(forecast.dt).2)")
                    .foregroundColor(.white)
                    .fontWeight(.bold)
                    .font(.system(size: 14))
                    .padding(.leading, 10)
                Text("\(formatDate(forecast.dt).0)\n     \(formatDate(forecast.dt).1)")
                    .foregroundColor(.gray)
                    .font(.system(size: 14))
                    .padding(.leading, 13)
            }
            .frame(width: 100)
            
            Image("\(getWeatherIconResource(forecast.weather[0].main))")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 60, height: 60)
                .padding(8)
            
            VStack (alignment: .leading) {
                Text("\(capitalizeFirstLetterEachWord(forecast.weather[0].description))")
                    .foregroundColor(.white)
                    .font(.system(size: 14))
                Text("🌡\(String(format: "%.1f°C",kelvinToCelsius(forecast.main.temp)))  \(String(format: "%.1f°C",kelvinToCelsius(forecast.main.temp_min)))-\(String(format: "%.1f°C",kelvinToCelsius(forecast.main.temp_max)))")
                    .foregroundColor(.white)
                    .font(.system(size: 13))
                Text("💦 Humidity: \(String(format: "%.0f %%", forecast.main.humidity))\n💨 Wind: \(String(format: "%.1f km/h", (forecast.wind.speed) * 3.6))\n🕛 Pressure: \(String(format: "%.0f hPa", forecast.main.pressure))")
                    .foregroundColor(.white)
                    .font(.system(size: 14))
            }
            
            Spacer()
        }
    }
}

private func getWeatherIconResource(_ weatherStatus: String) -> String {
    // Logic to map weather status to corresponding image names or resource identifiers
    switch weatherStatus {
    case "Rain":
        return "rainy_1"
    case "Clear":
        return "sunny"
    case "Clouds":
        return "cloudy_1"
    case "Snow":
        return "snowy"
    case "Thunderstorm":
        return "stormy"
    default:
        return "sunny"
    }
}

private func kelvinToCelsius(_ kelvin: Double) -> Double {
    return kelvin - 273.15 // Conversion formula from Kelvin to Celsius
}

private func capitalizeFirstLetterEachWord(_ input: String) -> String {
    var result = ""
    let words = input.components(separatedBy: " ")

    for word in words {
        if !word.isEmpty {
            let firstLetter = String(word.prefix(1)).capitalized
            let remainingLetters = String(word.dropFirst())
            result += firstLetter + remainingLetters + " "
        }
    }

    return result.trimmingCharacters(in: .whitespaces)
}

private func formatDate(_ dt: Int64) -> (String, String, String) {
    let date = Date(timeIntervalSince1970: TimeInterval(dt))
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "dd/MM/yyyy"
    let formattedDate = dateFormatter.string(from: date)
    
    dateFormatter.dateFormat = "EEEE"
    let dayOfWeek = dateFormatter.string(from: date)
    
    dateFormatter.dateFormat = "HH:mm"
    let formattedTime = dateFormatter.string(from: date)
    
    return (formattedDate, formattedTime, dayOfWeek)
}

extension Collection {
    subscript(safe index: Index) -> Element? {
        return indices.contains(index) ? self[index] : nil
    }
}

//#Preview{
//    MainView()
//}
