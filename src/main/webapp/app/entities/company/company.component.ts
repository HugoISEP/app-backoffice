import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';
import { CompanyDeleteDialogComponent } from 'app/entities/company/company-delete-dialog.component';

@Component({
  selector: 'jhi-company',
  templateUrl: './company.component.html',
})
export class CompanyComponent implements OnInit, OnDestroy {
  companies?: ICompany[];
  eventSubscriber?: Subscription;

  constructor(protected companyService: CompanyService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.companyService.getAll().subscribe((response: HttpResponse<ICompany[]>) => (this.companies = response.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInCompanies();
  }

  getId(index: number, company: ICompany): number {
    return company.id!;
  }

  delete(company: ICompany): void {
    const modalRef = this.modalService.open(CompanyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.company = company;
  }

  registerChangeInCompanies(): void {
    this.eventSubscriber = this.eventManager.subscribe('companyListModification', () => this.loadAll());
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }
}
