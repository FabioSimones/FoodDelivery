import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CaixaPedidos } from './caixa-pedidos';

describe('CaixaPedidos', () => {
  let component: CaixaPedidos;
  let fixture: ComponentFixture<CaixaPedidos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CaixaPedidos],
    }).compileComponents();

    fixture = TestBed.createComponent(CaixaPedidos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
