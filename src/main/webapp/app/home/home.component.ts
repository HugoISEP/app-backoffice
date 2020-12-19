import { Component, OnInit, OnDestroy } from '@angular/core';
import { combineLatest, Subscription } from 'rxjs';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { IPosition, Position } from 'app/shared/model/position.model';
import { PositionService } from 'app/entities/position/position.service';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { JhiEventManager } from 'ng-jhipster';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  positions?: IPosition[] | null = null;
  authSubscription?: Subscription;
  searchTerm = '';
  totalItems = 0;
  itemsPerPage = 5;
  page!: number;
  predicate!: string;
  ascending!: boolean;

  constructor(
    private accountService: AccountService,
    private positionService: PositionService,
    private loginModalService: LoginModalService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    protected eventManager: JhiEventManager
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
      if (account && !account.authorities.find(auth => auth === 'ROLE_ADMIN')) {
        this.handleNavigation();
      } else {
        this.positions = undefined;
      }
    });
  }

  loadAll(): void {
    this.positionService
      .getAllActive(
        {
          page: this.page - 1,
          size: this.itemsPerPage,
          sort: this.sort(),
        },
        this.searchTerm
      )
      .subscribe((response: HttpResponse<Position[]>) => this.onSuccess(response.body, response.headers));
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  getPositionId(index: number, position: IPosition): number {
    return position.id!;
  }

  togglePosition(position: IPosition): void {
    this.positionService.update({ ...position, status: !position.status }).subscribe(() => (position.status = !position.status));
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  // SORTING TABLE
  private handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      this.page = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      this.predicate = sort[0];
      this.ascending = sort[1] === 'asc';
      this.loadAll();
    }).subscribe();
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
      },
    });
  }

  private sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  private onSuccess(positions: Position[] | null, headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.positions = positions;
  }
}
