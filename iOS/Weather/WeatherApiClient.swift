//
//  WeatherApiClient.swift
//  Weather
//
//  Created by NTLG1 on 16/02/2024.
//

import Foundation

class WeatherApiClient {
    private let apiEndpoint = "https://api.openweathermap.org/data/2.5/weather"
    
    func getWeatherData(lat: Double, lon: Double, apiKey: String) async throws -> ResponseBody{
        guard let url = URL(string: "\(apiEndpoint)?lat=\(lat)&lon=\(lon)&appid=\(apiKey)") else {fatalError ("Something Went wrong...") }
        
        let urlRequest = URLRequest (url: url)
        let (data, res) = try await URLSession.shared.data(for: urlRequest)
//        guard (res as? HTTPURLResponse)?.statusCode == 200 else { fatalError("Error fetching weather data") }
        let decodedData = try JSONDecoder ().decode(ResponseBody.self, from: data)
        return decodedData
    }
}

struct ResponseBody: Decodable {
    var name: String
    var weather: [Weather]
    var main: Main
    var wind: Wind
    
    struct Weather: Decodable {
        var main: String
        var description: String
    }
    
    struct Main: Decodable {
        var temp: Double
        var temp_min: Double
        var temp_max: Double
        var humidity: Double
        var pressure: Double
    }
    
    struct Wind: Decodable {
        var speed: Double
    }
}


