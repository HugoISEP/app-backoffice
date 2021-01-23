import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { IPosition } from '../../shared/model/position.model';
import { PositionService } from './position.service';

@Component({
  selector: 'jhi-notification-sender-dialog',
  templateUrl: './notification-sender-dialog.component.html',
})
export class NotificationSenderDialogComponent {
  position?: IPosition;

  constructor(protected positionService: PositionService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmSending(id: number): void {
    this.activeModal.close();
    this.positionService.sendNotification(id).subscribe();
  }
}
