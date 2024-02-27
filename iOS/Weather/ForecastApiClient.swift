//
//  ForecastApiClient.swift
//  Weather
//
//  Created by NTLG1 on 16/02/2024.
//

import Foundation

class ForecastApiClient {
    private let apiEndpoint = "https://api.openweathermap.org/data/2.5/forecast"
    
    func getForecastData(lat: Double, lon: Double, apiKey: String) async throws -> ForecastResponseBody {
        guard let url = URL(string: "\(apiEndpoint)?lat=\(lat)&lon=\(lon)&appid=\(apiKey)") else {
            fatalError("Something Went wrong...")
        }
        
        let urlRequest = URLRequest(url: url)
        let (data, res) = try await URLSession.shared.data(for: urlRequest)
//        guard (res as? HTTPURLResponse)?.statusCode == 200 else {
//            fatalError("Error fetching forecast data")
//        }
        
        let decodedData = try JSONDecoder().decode(ForecastResponseBody.self, from: data)
        return decodedData
    }
}

struct ForecastResponseBody: Decodable {
    var list: [Forecast]
    
    struct Forecast: Decodable {
        var main: Main
        var weather: [Weather]
        var wind: Wind
        var dt: Int64 // Date and time in text format
        
        struct Main: Decodable {
            var temp: Double
            var temp_min: Double
            var temp_max: Double
            var humidity: Double
            var pressure: Double
        }
        
        struct Weather: Decodable {
            var main: String
            var description: String
        }
        
        struct Wind: Decodable {
            var speed: Double
        }
    }
}


