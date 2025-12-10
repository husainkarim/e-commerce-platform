import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ServerError } from './server-error';
import { ActivatedRoute } from '@angular/router';

describe('ServerError', () => {
  let component: ServerError;
  let fixture: ComponentFixture<ServerError>;

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
      imports: [ServerError],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServerError);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
