import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-backward',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './backward.component.html',
  styleUrl: './backward.component.css'
})
export class BackwardComponent {

  selectedCause = 'Varroosis';
  selectedEffect = 'CCD';
  checkCause = 'Varroosis';
  checkEffect = 'CCD';

  effectsResult: any = null;
  causesResult: any = null;
  checkResult: any = null;
  organicResult: any = null;
  allTreatments: any = null;

  constructor(private api: ApiService) {}

  findEffects() {
    this.api.getEffects(this.selectedCause).subscribe({
      next: (res) => { this.effectsResult = res; },
      error: (err) => console.error(err)
    });
  }

  findCauses() {
    this.api.getCauses(this.selectedEffect).subscribe({
      next: (res) => { this.causesResult = res; },
      error: (err) => console.error(err)
    });
  }

  checkChain() {
    this.api.checkCauseEffect(this.checkCause, this.checkEffect).subscribe({
      next: (res) => { this.checkResult = res; },
      error: (err) => console.error(err)
    });
  }

  getOrganicTreatments() {
    this.allTreatments = null;
    this.api.getOrganicTreatments().subscribe({
      next: (res) => { this.organicResult = res; },
      error: (err) => console.error(err)
    });
  }

  getAllTreatments() {
    this.organicResult = null;
    this.api.getTreatmentsInCategory('AntiVarroaTreatment').subscribe({
      next: (res) => { this.allTreatments = res; },
      error: (err) => console.error(err)
    });
  }
}
