// import { Component, OnInit, OnDestroy } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ApiService } from '../../services/api.service';

// @Component({
//   selector: 'app-cep-monitor',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './cep-monitor.component.html',
//   styleUrl: './cep-monitor.component.css'
// })
// export class CepMonitorComponent implements OnInit, OnDestroy {

//   hiveId = 14;
//   tempValue = 35.0;
//   tempZone = 'brood';
//   soundDb = 60.0;
//   soundFreq = 300.0;
//   weightValue = 42.0;
//   humidityValue = 65.0;

//   alarms: any[] = [];
//   eventLog: any[] = [];

//   // Simulator
//   simulatorRunning = false;
//   simulatorScenario = 'SWARMING';
//   simulatorStatus: any = null;

//   private pollingInterval: any;

//   constructor(private api: ApiService) {}

//   ngOnInit() {
//     this.pollingInterval = setInterval(() => {
//       this.refreshAlarms();
//       this.refreshSimulatorStatus();
//     }, 3000);
//   }

//   ngOnDestroy() {
//     if (this.pollingInterval) {
//       clearInterval(this.pollingInterval);
//     }
//   }

//   // ─── Manual Sensor Input ───

//   sendTemperature() {
//     this.api.insertTemperature(this.hiveId, this.tempValue, this.tempZone).subscribe({
//       next: (res) => {
//         this.alarms = res;
//         this.addLog('Temperature: ' + this.tempValue + '°C (' + this.tempZone + ')');
//       },
//       error: (err) => console.error(err)
//     });
//   }

//   sendSound() {
//     this.api.insertSound(this.hiveId, this.soundDb, this.soundFreq).subscribe({
//       next: (res) => {
//         this.alarms = res;
//         this.addLog('Sound: ' + this.soundDb + 'dB / ' + this.soundFreq + 'Hz');
//       },
//       error: (err) => console.error(err)
//     });
//   }

//   sendWeight() {
//     this.api.insertWeight(this.hiveId, this.weightValue).subscribe({
//       next: (res) => {
//         this.alarms = res;
//         this.addLog('Weight: ' + this.weightValue + 'kg');
//       },
//       error: (err) => console.error(err)
//     });
//   }

//   sendHumidity() {
//     this.api.insertHumidity(this.hiveId, this.humidityValue).subscribe({
//       next: (res) => {
//         this.alarms = res;
//         this.addLog('Humidity: ' + this.humidityValue + '%');
//       },
//       error: (err) => console.error(err)
//     });
//   }

//   refreshAlarms() {
//     this.api.getAlarms().subscribe({
//       next: (res) => { this.alarms = res; },
//       error: (err) => console.error(err)
//     });
//   }

//   // ─── Simulator Controls ───

//   startSimulator() {
//     this.api.startSimulator(this.simulatorScenario, this.hiveId).subscribe({
//       next: (res) => {
//         this.simulatorRunning = true;
//         this.addLog('SIMULATOR STARTED: ' + this.simulatorScenario);
//       },
//       error: (err) => console.error(err)
//     });
//   }

//   stopSimulator() {
//     this.api.stopSimulator().subscribe({
//       next: (res) => {
//         this.simulatorRunning = false;
//         this.addLog('SIMULATOR STOPPED');
//       },
//       error: (err) => console.error(err)
//     });
//   }

//   refreshSimulatorStatus() {
//     this.api.getSimulatorStatus().subscribe({
//       next: (res) => {
//         this.simulatorStatus = res;
//         this.simulatorRunning = res.running;
//       },
//       error: (err) => {}
//     });
//   }

//   private addLog(text: string) {
//     const time = new Date().toLocaleTimeString();
//     this.eventLog.unshift({ time, text });
//     if (this.eventLog.length > 20) this.eventLog.pop();
//   }
// }

import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-cep-monitor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cep-monitor.component.html',
  styleUrl: './cep-monitor.component.css'
})
export class CepMonitorComponent implements OnInit, OnDestroy {

  hiveId = 14;
  tempValue = 35.0;
  tempZone = 'brood';
  soundDb = 60.0;
  soundFreq = 300.0;
  weightValue = 42.0;
  humidityValue = 65.0;

  alarms: any[] = [];
  eventLog: any[] = [];

  simulatorRunning = false;
  simulatorScenario = 'SWARMING';
  simulatorStatus: any = null;
  private lastTickLogged = 0;

  private pollingInterval: any;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.pollingInterval = setInterval(() => {
      this.refreshAlarms();
      this.refreshSimulatorStatus();
    }, 3000);
  }

  ngOnDestroy() {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  sendTemperature() {
    this.api.insertTemperature(this.hiveId, this.tempValue, this.tempZone).subscribe({
      next: (res) => {
        this.alarms = res;
        this.addLog('Temperature: ' + this.tempValue + '°C (' + this.tempZone + ')');
      },
      error: (err) => console.error(err)
    });
  }

  sendSound() {
    this.api.insertSound(this.hiveId, this.soundDb, this.soundFreq).subscribe({
      next: (res) => {
        this.alarms = res;
        this.addLog('Sound: ' + this.soundDb + 'dB / ' + this.soundFreq + 'Hz');
      },
      error: (err) => console.error(err)
    });
  }

  sendWeight() {
    this.api.insertWeight(this.hiveId, this.weightValue).subscribe({
      next: (res) => {
        this.alarms = res;
        this.addLog('Weight: ' + this.weightValue + 'kg');
      },
      error: (err) => console.error(err)
    });
  }

  sendHumidity() {
    this.api.insertHumidity(this.hiveId, this.humidityValue).subscribe({
      next: (res) => {
        this.alarms = res;
        this.addLog('Humidity: ' + this.humidityValue + '%');
      },
      error: (err) => console.error(err)
    });
  }

  refreshAlarms() {
    this.api.getAlarms().subscribe({
      next: (res) => { this.alarms = res; },
      error: (err) => console.error(err)
    });
  }

  startSimulator() {
    this.lastTickLogged = 0;
    this.api.startSimulator(this.simulatorScenario, this.hiveId).subscribe({
      next: (res) => {
        this.simulatorRunning = true;
        this.addLog('SIMULATOR STARTED: ' + this.simulatorScenario);
      },
      error: (err) => console.error(err)
    });
  }

  stopSimulator() {
    this.api.stopSimulator().subscribe({
      next: (res) => {
        this.simulatorRunning = false;
        this.addLog('SIMULATOR STOPPED');
      },
      error: (err) => console.error(err)
    });
  }

  refreshSimulatorStatus() {
    this.api.getSimulatorStatus().subscribe({
      next: (res) => {
        this.simulatorStatus = res;
        this.simulatorRunning = res.running;

        if (res.running && res.tickCount > this.lastTickLogged) {
          this.lastTickLogged = res.tickCount;
          this.addLog(
            '[Tick ' + res.tickCount + '] ' + res.phase +
            ' | Temp: ' + res.lastTemp.toFixed(1) + '°C' +
            ' | Sound: ' + res.lastSound.toFixed(1) + 'dB' +
            ' | Weight: ' + res.lastWeight.toFixed(1) + 'kg' +
            ' | Alarms: ' + res.activeAlarms.length
          );
        }

        if (!res.running && this.lastTickLogged > 0) {
          this.addLog('SIMULATION COMPLETE');
          this.lastTickLogged = 0;
        }
      },
      error: (err) => {}
    });
  }

  private addLog(text: string) {
    const time = new Date().toLocaleTimeString();
    this.eventLog.unshift({ time, text });
    if (this.eventLog.length > 30) this.eventLog.pop();
  }
}