import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Jogo {

    private static final String MSG_SOLICITAR_JOGADA = "Sua vez [x y]: ";
    private static final String MSG_INICIO_JOGO = "Quem come\u00E7a?\n1 = M\u00E1quina\n2 = Usu\u00E1rio: ";
    private static final String MSG_PERDEU = "Voc\u00EA precisa treinar mais um pouco ;(";
    private static final String MSG_GANHOU = "Vossa Excel\u00EAncia faturou! :-O";
    private static final String MSG_EMPATOU = "Empatou!";

    private final char casaLivre;
    private final char maquina;
    private final char usuario;
    private final boolean debugMinimax;

    private Jogada jogadaDaMaquina;
    private Scanner scanner = new Scanner(System.in);
    private int[][] tabuleiro = new int[3][3];

    public Jogo(char casaLivre, char maquina, char usuario, boolean debugMinimax) {
        this.casaLivre = casaLivre;
        this.maquina = maquina;
        this.usuario = usuario;
        this.debugMinimax = debugMinimax;
    }

    public void iniciar() {
        imprimirTabuleiro();

        System.out.println(MSG_INICIO_JOGO);

        int quemComeca = scanner.nextInt();
        System.out.println();
        if (quemComeca == 1) {
            solicitarJogadaDaMaquina();
            imprimirTabuleiro();
        }

        while (!fimDeJogo()) {
            solicitarJogadaDoUsuario();
            imprimirTabuleiro();

            if (fimDeJogo()) break;

            solicitarJogadaDaMaquina();
            imprimirTabuleiro();
        }

        if (venceu(1)) {
            System.out.println(MSG_PERDEU);
        } else if (venceu(2)) {
            System.out.println(MSG_GANHOU);
        } else {
            System.out.println(MSG_EMPATOU);
        }
    }

    private void solicitarJogadaDoUsuario() {
        System.out.println(MSG_SOLICITAR_JOGADA);
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        jogar(new Jogada(x, y), 2);
    }


    private void solicitarJogadaDaMaquina() {
        if (jogoEstaNoInicio()) {
            Random random = new Random();
            jogar(new Jogada(random.nextInt(3), random.nextInt(3)), 1);
            return;
        }

        minimax(0, 1);
        jogar(jogadaDaMaquina, 1);
    }

    private boolean jogoEstaNoInicio() {
        return obterCasasLivres().size() == 9;
    }


    private boolean fimDeJogo() {
        // O jogo acaba quando alguém vence ou empata (sem demais jogadas possíveis)
        return (venceu(1) || venceu(2) || obterCasasLivres().isEmpty());
    }

    private boolean venceu(int jogador) {
        // Diagonal
        if ((tabuleiro[0][0] == tabuleiro[1][1] && tabuleiro[0][0] == tabuleiro[2][2] && tabuleiro[0][0] == jogador)
                || (tabuleiro[0][2] == tabuleiro[1][1] && tabuleiro[0][2] == tabuleiro[2][0] && tabuleiro[0][2] == jogador)) {
            return true;
        }

        // Linha ou Coluna
        for (int i = 0; i < 3; ++i) {
            if ((tabuleiro[i][0] == tabuleiro[i][1] && tabuleiro[i][0] == tabuleiro[i][2] && tabuleiro[i][0] == jogador)
                    || (tabuleiro[0][i] == tabuleiro[1][i] && tabuleiro[0][i] == tabuleiro[2][i] && tabuleiro[0][i] == jogador)) {
                return true;
            }
        }

        return false;
    }

    private List<Jogada> obterCasasLivres() {
        final List<Jogada> pontosDisponiveis = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (tabuleiro[i][j] == 0) {
                    pontosDisponiveis.add(new Jogada(i, j));
                }
            }
        }
        return pontosDisponiveis;
    }

    private void jogar(Jogada jogada, int jogador) {
        tabuleiro[jogada.x][jogada.y] = jogador;
    }

    private void imprimirTabuleiro() {
        System.out.println();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                System.out.print(obterRepresentacaoDaCasa(tabuleiro[i][j]) + " ");
            }
            System.out.println();

        }
    }

    private char obterRepresentacaoDaCasa(int jogador) {
        return jogador == 0 ? casaLivre : jogador == 1 ? maquina : usuario;
    }

    private int minimax(int profundidade, int vez) {
        if (venceu(1)) return +1;
        if (venceu(2)) return -1;

        List<Jogada> casasLivres = obterCasasLivres();
        if (casasLivres.isEmpty()) return 0;

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

        for (int i = 0; i < casasLivres.size(); ++i) {
            Jogada jogada = casasLivres.get(i);
            if (vez == 1) {
                jogar(jogada, 1);
                int nota = minimax(profundidade + 1, 2);
                max = Math.max(nota, max);
                if (profundidade == 0) {
                    if (debugMinimax) {
                        System.out.println("Nota da jogada " + jogada.toString() + " = " + nota);
                    }
                }
                if (nota >= 0) {
                    if(profundidade == 0) jogadaDaMaquina = jogada;
                }
                if (nota == 1) {
                    // Se a nota foi 1, então imediatamente pára
                    tabuleiro[jogada.x][jogada.y] = 0;
                    break;
                }
                if (i == casasLivres.size()-1 && max < 0) {
                    if (profundidade == 0) jogadaDaMaquina = jogada;
                }
            } else if (vez == 2) {
                jogar(jogada, 2);
                int nota = minimax(profundidade + 1, 1);
                min = Math.min(nota, min);
                if (min == -1) {
                    // Se a nota foi -1, então imediatamente pára
                    tabuleiro[jogada.x][jogada.y] = 0;
                    break;
                }
            }
            // Limpar essa casa
            tabuleiro[jogada.x][jogada.y] = 0;
        }

        return vez == 1 ? max : min;
    }
}
