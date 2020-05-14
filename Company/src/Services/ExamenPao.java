import org.graalvm.compiler.lir.amd64.vector.AMD64VectorUnary;

import java.util.Objects;
import java.util.ArrayList;
class Automobil {
   String marca,model;
   double capacitate;
   double pret;

    public String getMarca() {
        return marca;
    }

    public String getModel() {
        return model;
    }

    public double getPret() {
        return pret;
    }

    public double getCapacitate() {
        return capacitate;
    }

    public void setCapacitate(double capacitate) {
        this.capacitate = capacitate;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Automobil{" +
                "marca='" + marca + '\'' +
                ", model='" + model + '\'' +
                ", capacitate=" + capacitate +
                ", pret=" + pret +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Automobil automobil = (Automobil) o;
        return Double.compare(automobil.capacitate, capacitate) == 0 &&
                Double.compare(automobil.pret, pret) == 0 &&
                Objects.equals(marca, automobil.marca) &&
                Objects.equals(model, automobil.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marca, model, capacitate, pret);
    }

}
public class ExamenPao {
    public static void main(String[] args) {
        ArrayList<Automobil> v = new ArrayList<Automobil>(3);
        v.get(0).setMarca("BMW");
        v.get(0).setModel("X3");
        v.get(0).setCapacitate(434.0);
        v.get(0).setPret(43254.0);
        v.get(1).setMarca("Maseratti");
        v.get(1).setModel("X5");
        v.get(1).setCapacitate(434.0);
        v.get(1).setPret(3444.0);
        v.get(2).setMarca("Ferrari");
        v.get(2).setModel("X4");
        v.get(2).setCapacitate(434.0);
        v.get(2).setPret(78854.0);

        Consumer<Automobil> consumer = (vec)
    }
}