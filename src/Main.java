import java.io.*;
import com.fuzzylite.Engine;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class Main {

    static Engine engine;
    static  InputVariable input_one, input_two, input_three;
    static OutputVariable output;
    static double[] inputoneMaxMin = {1.079,-3.57};
    static double[] inputtwoMaxMin = {1.237,-2.206};
    static double[] outputMaxMin = {1.202,-2.945};
    static int populationSize=100;
    static int[][] population=new int[populationSize*2][9];
    static List reglas;
    public static void main(String[] args) throws IOException{
    //tenemos 3 entradas y una salida (granularidad en todos de bajo medio y alto)
    //hay que probar las combinaciones a ver cual tiene menos error jeje.
    //27 posibles combinaciones me parece 3^3. <--- estás son fijas son todos los posibles casos
    //a cada conjunto de regla hay que hacerle las combinaciones de salidas bajo medio alto y ver cual clasifica mejor
	System.out.println("Fuzzy Resilencia");
    double []fitness=new double[populationSize*2];
    Random rnd=new Random();

    for(int x=0;x<populationSize;x++)
    {
        for(int j=0;j<9;j++) {
            population[x][j] = rnd.nextInt(3); //lleno la poblacion de inviduos
        }
        fitness[x]=Load_FIS(population[x]);
    }

    int generations=0;
    while(generations<250)
    {
        generations++;
        for(int x=populationSize;x< population.length;x=x+2) {
            //torneo binario 1
            int sol1 = rnd.nextInt(populationSize);
            int sol2 = rnd.nextInt(populationSize);
            int padre1, padre2;
            if (fitness[sol1] < fitness[sol2])
                padre1 = sol1;
            else
                padre1 = sol2;
            //torneo binario 2
            sol2 = rnd.nextInt(populationSize);
            sol2 = rnd.nextInt(populationSize);
            if (fitness[sol1] < fitness[sol2])
                padre2 = sol1;
            else
                padre2 = sol2;
            //cruza single point crossover
            int point=rnd.nextInt(9);
            for(int j=0;j<point;j++) {
                population[x][j] = population[padre1][j];
                population[x + 1][j] = population[padre2][j];
            }
            for(int j=point;j<9;j++) {
                population[x][j] = population[padre2][j];
                population[x + 1][j] = population[padre1][j];
            }
            //mutacion
            for(int j=0;j<9;j++)
            {
                if(Math.random()<(1.0/9.0))
                {
                    population[x][j]=rnd.nextInt(3);
                }
                if(Math.random()<(1.0/9.0))
                {
                    population[x+1][j]=rnd.nextInt(3);
                }
            }
            //fitness evaluation
            fitness[x]=Load_FIS(population[x]);
            fitness[x+1]=Load_FIS(population[x+1]);
        }
        //seleccion ambiental, aqui seria bueno solo hacer un sort de la poblacion de mejor a peor.
        quicksort(fitness,0, populationSize-1);
        //System.out.println("Generation "+generations);
        //System.out.println("Mejor Fitness "+fitness[0]);
    }
    Load_FIS(population[0]);
    System.out.println("Fitness: "+fitness[0]);
    for(int x=0;x<reglas.size();x++)
    {
        System.out.println(reglas.get(x));
    }
    
    }




    static void quicksort(double[] arreglo, int izquierda, int derecha)
    {
        if (izquierda < derecha)
        {
            int indiceParticion = particion(arreglo, izquierda, derecha);
            quicksort(arreglo, izquierda, indiceParticion);
            quicksort(arreglo, indiceParticion + 1, derecha);
        }
    }

    static int particion(double[] arreglo, int izquierda, int derecha)
    {
        double pivote = arreglo[izquierda];
        while (true)
        {
        /*
        Acercar los extremos hacia el centro mientras se encuentren elementos ordenados
         */
            while (arreglo[izquierda] < pivote)
            {
                izquierda++;
            }

            while (arreglo[derecha] > pivote)
            {
                derecha--;
            }
            // Si los extremos se cruzaron o superaron, entonces toda la porción del arreglo estaba ordenada
            if (izquierda >= derecha)
            {
                // Regresamos el índice para indicar hasta qué posición el arreglo está en orden
                return derecha;
            }
            else
            {
                // Si no estuvieron ordenados, vamos a hacer el intercambio
                int []solizquierdo=population[izquierda];
                int []solderecho=population[derecha];
                population[izquierda]=null;
                population[derecha]=null;
                population[izquierda]=solderecho;
                population[derecha]=solizquierdo;
                double temporal = arreglo[izquierda];
                arreglo[izquierda] = arreglo[derecha];
                arreglo[derecha] = temporal;
                // Y acercamos en 1 los extremos
                derecha--; izquierda++;

            }
            // El while se repite hasta que izquierda >= derecha
        }
    }

    private static double Load_FIS(int[] cromosoma) throws IOException
    {
        engine=new Engine();
        engine.setName("Probabilides-operadores");
        input_two = new InputVariable();
        input_two.setName("TSSE");
        input_two.setRange(0.0, 1.0);
        input_two.addTerm(new Triangle("LOW", -0.4, 0.0, 0.4));
        input_two.addTerm(new Triangle("MID", 0.1, 0.5, 0.9));
        input_two.addTerm(new Triangle("HIGH", 0.6, 1.0, 1.4));
        engine.addInputVariable(input_two);

        input_one = new InputVariable();
        input_one.setName("SD");
        input_one.setRange(0.0, 1.0);
        input_one.addTerm(new Triangle("LOW", -0.4, 0.0, 0.4));
        input_one.addTerm(new Triangle("MID", 0.1, 0.5, 0.9));
        input_one.addTerm(new Triangle("HIGH", 0.6, 1.0, 1.4));
        engine.addInputVariable(input_one);

        input_three = new InputVariable();
        input_three.setName("SR");
        input_three.setRange(0.0, 1.0);
        input_three.addTerm(new Triangle("LOW", -0.4, 0.0, 0.4));
        input_three.addTerm(new Triangle("MID", 0.1, 0.5, 0.9));
        input_three.addTerm(new Triangle("HIGH", 0.6, 1.0, 1.4));
        engine.addInputVariable(input_three);


        output = new OutputVariable();
        output.setName("CFM");
        output.setRange(0.0, 1.0);
        output.addTerm(new Triangle("LOW", -0.4, 0.0, 0.4));//probability.addTerm(new Triangle("LOW", 0.000, 0.250, 0.500));
        output.addTerm(new Triangle("MID", 0.1, 0.5, 0.9));//probability.addTerm(new Triangle("MID", 0.250, 0.500, 0.750));
        output.addTerm(new Triangle("HIGH", 0.6, 1.0, 1.4));//probability.addTerm(new Triangle("HIGH", 0.500, 0.750, 1.000));
        engine.addOutputVariable(output);

        RuleBlock ruleBlock = new RuleBlock();
        String tmpoutput="";
        switch(cromosoma[0])
        {
            case 0:
                tmpoutput="LOW";
                break;
            case 1:
                tmpoutput="MID";
                break;
            case 2:
                tmpoutput="HIGH";
                break;
            default:
                tmpoutput="ERROR";
                break;
        }
        ruleBlock.addRule(Rule.parse("if TSSE is LOW and SD is LOW then CFM is "+tmpoutput+"",engine));
        switch(cromosoma[1])
        {
            case 0:
                tmpoutput="LOW";
                break;
            case 1:
                tmpoutput="MID";
                break;
            case 2:
                tmpoutput="HIGH";
                break;
            default:
                tmpoutput="ERROR";
                break;
        }
        ruleBlock.addRule(Rule.parse("if TSSE is LOW and SD is MID then CFM is "+tmpoutput+"",engine));
        switch(cromosoma[2])
        {
            case 0:
                tmpoutput="LOW";
                break;
            case 1:
                tmpoutput="MID";
                break;
            case 2:
                tmpoutput="HIGH";
                break;
            default:
                tmpoutput="ERROR";
                break;
        }
        ruleBlock.addRule(Rule.parse("if TSSE is LOW and SD is HIGH then CFM is "+tmpoutput+"",engine));
        switch(cromosoma[3])
        {
            case 0:
                tmpoutput="LOW";
                break;
            case 1:
                tmpoutput="MID";
                break;
            case 2:
                tmpoutput="HIGH";
                break;
            default:
                tmpoutput="ERROR";
                break;
        }
        ruleBlock.addRule(Rule.parse("if TSSE is MID and SD is LOW then CFM is "+tmpoutput+"",engine));
        switch(cromosoma[4])
        {
            case 0:
                tmpoutput="LOW";
                break;
            case 1:
                tmpoutput="MID";
                break;
            case 2:
                tmpoutput="HIGH";
                break;
            default:
                tmpoutput="ERROR";
                break;
        }
        ruleBlock.addRule(Rule.parse("if TSSE is MID and SD is MID then CFM is "+tmpoutput+"",engine));
        switch(cromosoma[5])
        {
            case 0:
                tmpoutput="LOW";
                break;
            case 1:
                tmpoutput="MID";
                break;
            case 2:
                tmpoutput="HIGH";
                break;
            default:
                tmpoutput="ERROR";
                break;
        }
        ruleBlock.addRule(Rule.parse("if TSSE is MID and SD is HIGH then CFM is "+tmpoutput+"",engine));
        switch(cromosoma[6])
        {
            case 0:
                tmpoutput="LOW";
                break;
            case 1:
                tmpoutput="MID";
                break;
            case 2:
                tmpoutput="HIGH";
                break;
            default:
                tmpoutput="ERROR";
                break;
        }
        ruleBlock.addRule(Rule.parse("if TSSE is HIGH and SD is LOW then CFM is "+tmpoutput+"",engine));
        switch(cromosoma[7])
        {
            case 0:
                tmpoutput="LOW";
                break;
            case 1:
                tmpoutput="MID";
                break;
            case 2:
                tmpoutput="HIGH";
                break;
            default:
                tmpoutput="ERROR";
                break;
        }
        ruleBlock.addRule(Rule.parse("if TSSE is HIGH and SD is MID then CFM is "+tmpoutput+"",engine));
        switch(cromosoma[8])
        {
            case 0:
                tmpoutput="LOW";
                break;
            case 1:
                tmpoutput="MID";
                break;
            case 2:
                tmpoutput="HIGH";
                break;
            default:
                tmpoutput="ERROR";
                break;
        }
        ruleBlock.addRule(Rule.parse("if TSSE is HIGH and SD is HIGH then CFM is "+tmpoutput+"",engine));
        reglas=ruleBlock.getRules();
        engine.addRuleBlock(ruleBlock);
        //
        engine.configure("Minimum","Maximum","Minimum","Maximum","Centroid","General");
        StringBuilder status = new StringBuilder();
        if (!engine.isReady(status)) {
            throw new RuntimeException("Engine not ready. "
                    + "The following errors were encountered:\n" + status.toString());
        }


        FileReader fr=new FileReader("EUREKA2.csv");
        BufferedReader bf=new BufferedReader(fr);
        bf.readLine();//tiro los encabezados que no ocupo para nada.
        double error=0.0;
        int ejemplos=0;
        double sum=0.0;
        while(bf.ready())
        {
            ejemplos++;
            StringTokenizer temp=new StringTokenizer(bf.readLine(),",");
            double inputone=Double.valueOf(temp.nextToken());
            inputone=(inputone- inputoneMaxMin[1])/(inputoneMaxMin[0]- inputoneMaxMin[1]);
            double input2=Double.valueOf(temp.nextToken());
            input2=(input2- inputtwoMaxMin[1])/(inputtwoMaxMin[0]- inputtwoMaxMin[1]);
            double output=Double.valueOf(temp.nextToken());
            output=(output- outputMaxMin[1])/(outputMaxMin[0]- outputMaxMin[1]);
            //valores normalizados entre 0 y 1
            engine.setInputValue("TSSE", inputone);
            engine.setInputValue("SD", input2);
            engine.process();
            error+=Math.abs(output-engine.getOutputValue("CFM"));
            sum+=output;
        }
        System.out.println("Error "+error/sum);
        return error/sum;
    }



}
