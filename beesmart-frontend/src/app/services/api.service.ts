import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  runDiagnosis(request: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/diagnosis/run`, request);
  }

  getEffects(cause: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/backward/effects`, {
      params: new HttpParams().set('cause', cause)
    });
  }

  getCauses(effect: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/backward/causes`, {
      params: new HttpParams().set('effect', effect)
    });
  }

  checkCauseEffect(cause: string, effect: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/backward/check`, {
      params: new HttpParams().set('cause', cause).set('effect', effect)
    });
  }

  getOrganicTreatments(): Observable<any> {
    return this.http.get(`${this.baseUrl}/backward/organic-treatments`);
  }

  getTreatmentsInCategory(category: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/backward/treatments`, {
      params: new HttpParams().set('category', category)
    });
  }

  insertTemperature(hiveId: number, temperature: number, zone: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/cep/temperature`, null, {
      params: new HttpParams()
        .set('hiveId', hiveId).set('temperature', temperature).set('zone', zone)
    });
  }

  insertSound(hiveId: number, decibels: number, frequency: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/cep/sound`, null, {
      params: new HttpParams()
        .set('hiveId', hiveId).set('decibels', decibels).set('frequency', frequency)
    });
  }

  insertWeight(hiveId: number, weightKg: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/cep/weight`, null, {
      params: new HttpParams().set('hiveId', hiveId).set('weightKg', weightKg)
    });
  }

  insertHumidity(hiveId: number, humidity: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/cep/humidity`, null, {
      params: new HttpParams().set('hiveId', hiveId).set('humidity', humidity)
    });
  }

  getAlarms(): Observable<any> {
    return this.http.get(`${this.baseUrl}/cep/alarms`);
  }

  startSimulator(scenario: string, hiveId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/simulator/start`, null, {
      params: new HttpParams().set('scenario', scenario).set('hiveId', hiveId),
      responseType: 'text'
    });
  }

  stopSimulator(): Observable<any> {
    return this.http.post(`${this.baseUrl}/simulator/stop`, null, {
      responseType: 'text'
    });
  }

  getSimulatorStatus(): Observable<any> {
    return this.http.get(`${this.baseUrl}/simulator/status`);
  }

  getTreatmentTemplate(hiveType: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/templates/treatment`, {
      params: new HttpParams().set('hiveType', hiveType)
    });
  }

  getSeasonalTemplate(breed: string, month: number, avgTemp: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/templates/seasonal`, {
      params: new HttpParams()
        .set('breed', breed).set('month', month).set('avgTemp', avgTemp)
    });
  }
}
