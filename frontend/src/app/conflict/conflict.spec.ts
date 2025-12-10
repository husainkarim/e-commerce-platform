import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Conflict } from './conflict';
import { ActivatedRoute } from '@angular/router';

describe('Conflict', () => {
  let component: Conflict;
  let fixture: ComponentFixture<Conflict>;

  // --- NEW: Define a minimal mock for ActivatedRoute ---
  const mockActivatedRoute = {
    // Error components often don't need parameters, but they need the service provided
    snapshot: {},
    params: {
        subscribe: () => ({})
    },
    queryParamMap: {
        subscribe: () => ({})
    }
  };
  // -----------------------------------------------------


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Conflict],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Conflict);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
