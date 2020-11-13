import { Component } from '@angular/core';
import { User } from '../../core/user/user.model';
import { UserService } from '../../core/user/user.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

@Component({
  selector: 'jhi-user-management-delete-dialog',
  templateUrl: './user-management-delete-dialog.component.html',
})
export class UserManagementDeleteDialogComponent {
  user?: User;

  constructor(private userService: UserService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userService.delete(id).subscribe(() => {
      this.eventManager.broadcast('userListModification');
      this.activeModal.close();
    });
  }
}
