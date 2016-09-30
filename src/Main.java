public class Main {

    public static void main(String[] args) {
        final char casaLivre = args.length > 0 ? args[0].charAt(0) : '-';
        final char maquina = args.length > 1 ? args[1].charAt(0) : 'X';
        final char usuario = args.length > 2 ? args[2].charAt(0) : 'O';
        final boolean debugMinimax = args.length > 3 ? Boolean.parseBoolean(args[3]) : true;

        final Jogo jogo = new Jogo(casaLivre, maquina, usuario, debugMinimax);
        jogo.iniciar();
    }
}