import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-diagnosis',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './diagnosis.component.html',
  styleUrl: './diagnosis.component.css'
})
export class DiagnosisComponent {

  hive: any = {
    id: 14, hiveType: 'LR', breed: 'Carniolan',
    frameCount: 8, broodFrameCount: 5, honeyStockKg: 12,
    queenAgeMonths: 14, weightKg: 38, weightLastMonthKg: 45,
    hasSuper: true, organicProduction: false, weakEggLaying: false
  };

  weather: any = {
    month: 8, avgWeeklyTemp: 25, currentTemp: 28, forecast3DayTemp: 22
  };

  activeBloom: string = 'none';
  miteDropCount: number = 18;
  selectedSymptoms: string[] = ['deformed_wings', 'visible_mites', 'reduced_population'];
  result: any = null;

  availableSymptoms = [
    { name: 'deformed_wings', label: 'Deformed wings (DWV)' },
    { name: 'visible_mites', label: 'Visible mites on bees' },
    { name: 'reduced_population', label: 'Reduced population' },
    { name: 'queen_cells', label: 'Queen cells present' },
    { name: 'ropiness_test_positive', label: 'Ropiness test positive' },
    { name: 'smell_of_decay', label: 'Smell of decay' },
    { name: 'mosaic_brood', label: 'Mosaic brood pattern' },
    { name: 'sunken_cappings', label: 'Sunken cappings' },
    { name: 'dark_sticky_larvae', label: 'Dark sticky larvae' },
    { name: 'yellowish_larvae', label: 'Yellowish-brown larvae' },
    { name: 'sour_smell', label: 'Sour smell' },
    { name: 'larvae_dead_before_capping', label: 'Larvae dead before capping' },
    { name: 'no_ropiness', label: 'No ropiness (thread test)' },
    { name: 'reduced_pollen_intake', label: 'Reduced pollen intake' },
    { name: 'mummified_larvae', label: 'Mummified white/grey larvae' },
    { name: 'chalky_larvae_at_entrance', label: 'Chalky larvae at entrance' },
    { name: 'high_humidity', label: 'High humidity (>70%)' },
    { name: 'cold_location', label: 'Cold/shaded location' },
    { name: 'moth_larvae_on_comb', label: 'Moth larvae on comb' },
    { name: 'tunnels_webbing', label: 'Tunnels and webbing' },
    { name: 'damaged_comb', label: 'Damaged comb' },
    { name: 'weak_colony_under_5', label: 'Weak colony (<5 frames)' },
    { name: 'poor_comb_storage', label: 'Poor comb storage' },
    { name: 'diarrhea_on_landing_board', label: 'Diarrhea on landing board' },
    { name: 'swollen_abdomen', label: 'Swollen abdomen' },
    { name: 'spring_mortality', label: 'Spring mortality' },
    { name: 'weak_spring_development', label: 'Weak spring development' },
    { name: 'sudden_disappearance', label: 'Sudden disappearance of bees *' },
    { name: 'treated_with_pesticides', label: 'Treated with pesticides (30d) *' },
    { name: 'varroa_history_6m', label: 'Varroa history (6 months) *' },
    { name: 'untouched_stores', label: 'Stores untouched' },
    { name: 'no_dead_bees', label: 'No dead bees found' },
    { name: 'brood_without_bees', label: 'Brood without nurse bees' },
    { name: 'gradual_population_decline', label: 'Gradual population decline *' },
    { name: 'varroa_nosema_history_6m', label: 'Varroa/Nosema history (6m) *' },
    { name: 'antibiotics_60d', label: 'Antibiotics used (60 days) *' },
    { name: 'reduced_egg_laying', label: 'Reduced egg laying' },
    { name: 'weak_brood', label: 'Weak brood' },
    { name: 'reduced_nectar_intake', label: 'Reduced nectar intake' }
  ];

  constructor(private api: ApiService) {}

  toggleSymptom(name: string) {
    const index = this.selectedSymptoms.indexOf(name);
    if (index > -1) {
      this.selectedSymptoms.splice(index, 1);
    } else {
      this.selectedSymptoms.push(name);
    }
  }

  runDiagnosis() {
    const blooms = this.activeBloom === 'none'
      ? [{ plant: 'acacia', active: false }, { plant: 'linden', active: false }]
      : [{ plant: this.activeBloom, active: true }];

    const symptoms = this.selectedSymptoms.map(name => {
      const isSpecific = ['sudden_disappearance', 'treated_with_pesticides', 'varroa_history_6m',
        'gradual_population_decline', 'varroa_nosema_history_6m', 'antibiotics_60d'].includes(name);
      return { name, specific: isSpecific };
    });

    const request = {
      hive: this.hive,
      weather: this.weather,
      blooms: blooms,
      symptoms: symptoms,
      miteDropCount: this.miteDropCount,
      treatmentHistory: []
    };

    this.api.runDiagnosis(request).subscribe({
      next: (res) => { this.result = res; },
      error: (err) => { console.error('Diagnosis error:', err); }
    });
  }
}
