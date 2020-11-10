import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { IPosition } from '../../shared/model/position.model';
import { PositionService } from './position.service';

@Component({
  selector: 'jhi-position-delete',
  templateUrl: './position-delete-dialog.component.html',
})
export class PositionDeleteDialogComponent {
  position?: IPosition;

  constructor(protected positionService: PositionService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.positionService.delete(id).subscribe(() => {
      this.eventManager.broadcast('positionListModification');
      this.activeModal.close();
    });
  }
}
