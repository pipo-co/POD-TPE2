package ar.edu.itba.pod.models;

public enum CityCSVDatasource {
    BUE {
        @Override
        public Tree treeFromCSV(final String[] csv) {
            return new Tree(csv[2], csv[4], csv[7]);
        }

        @Override
        public Neighbourhood hoodFromCSV(final String[] csv) {
            return new Neighbourhood(csv[0], Long.parseLong(csv[1]));
        }
    },
    VAN {
        @Override
        public Tree treeFromCSV(final String[] csv) {
            return new Tree(csv[2], csv[6], csv[12]);
        }

        @Override
        public Neighbourhood hoodFromCSV(final String[] csv) {
            return new Neighbourhood(csv[0], Long.parseLong(csv[1]));
        }
    },
    ;

    public abstract Tree            treeFromCSV(final String[] csvLine);
    public abstract Neighbourhood   hoodFromCSV(final String[] csvLine);
}