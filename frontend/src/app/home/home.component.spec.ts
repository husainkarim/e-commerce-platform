import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BehaviorSubject } from 'rxjs';
import { HomeComponent } from './home.component';
import { AuthServiceService } from '../auth-service.service';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let isLoggedInSubject: BehaviorSubject<boolean>;
  let authService: { isLoggedIn$: BehaviorSubject<boolean> };

  beforeEach(async () => {
    isLoggedInSubject = new BehaviorSubject<boolean>(false);
    authService = { isLoggedIn$: isLoggedInSubject };

    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [{ provide: AuthServiceService, useValue: authService }]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update login signal when auth state changes', () => {
    expect(component.isLoggedInSignal()).toBeFalse();
    isLoggedInSubject.next(true);
    expect(component.isLoggedInSignal()).toBeTrue();
  });
});
