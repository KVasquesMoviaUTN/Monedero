package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {

  }//Duplicated Code

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    esNegativo(cuanto);//Duplicated code

    if (getMovimientosDelDia() >= 3) {//Message chains
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  public void sacar(double cuanto) {
    esNegativo(cuanto);

    double limite = 1000 - getMontoExtraidoA();//Duplicated Code
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }

  public void esNegativo(double cuanto){//Para arreglar large method
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA() {//Feature Envy
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(LocalDate.now()))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public long getMovimientosDelDia() {
    return getMovimientos().stream().filter(movimiento -> movimiento.fueDepositado(LocalDate.now())).count();
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  public boolean isMontoNegativo(double cuanto) {
    return cuanto < 0;
  }

}
