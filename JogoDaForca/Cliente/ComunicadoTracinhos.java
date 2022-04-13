public class ComunicadoTracinhos extends Comunicado
{
    private String tracinhostxt;

    public ComunicadoTracinhos (String tracinhotxt){
        this.tracinhostxt = tracinhotxt;
    }

    @Override
    public String toString() {
        return this.tracinhostxt;
    }
}
