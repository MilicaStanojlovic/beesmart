import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-templates',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './templates.component.html',
  styleUrl: './templates.component.css'
})
export class TemplatesComponent {

  selectedHiveType = 'LR';
  selectedBreed = 'Carniolan';
  selectedMonth = 5;
  avgTemp = 18;

  treatments: any[] = [];
  seasonal: any[] = [];
  seasonalLoaded = false;

  months = [
    { value: 1, label: 'January' }, { value: 2, label: 'February' },
    { value: 3, label: 'March' }, { value: 4, label: 'April' },
    { value: 5, label: 'May' }, { value: 6, label: 'June' },
    { value: 7, label: 'July' }, { value: 8, label: 'August' },
    { value: 9, label: 'September' }, { value: 10, label: 'October' },
    { value: 11, label: 'November' }, { value: 12, label: 'December' }
  ];

  constructor(private api: ApiService) {}

  loadTreatments() {
    this.api.getTreatmentTemplate(this.selectedHiveType).subscribe({
      next: (res) => { this.treatments = res; },
      error: (err) => console.error(err)
    });
  }

  loadSeasonal() {
    this.seasonalLoaded = false;
    this.api.getSeasonalTemplate(this.selectedBreed, this.selectedMonth, this.avgTemp).subscribe({
      next: (res) => { this.seasonal = res; this.seasonalLoaded = true; },
      error: (err) => console.error(err)
    });
  }
}
