import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CozinhaPedidos } from './cozinha-pedidos';

describe('CozinhaPedidos', () => {
  let component: CozinhaPedidos;
  let fixture: ComponentFixture<CozinhaPedidos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CozinhaPedidos],
    }).compileComponents();

    fixture = TestBed.createComponent(CozinhaPedidos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
