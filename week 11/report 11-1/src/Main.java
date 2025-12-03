import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Main {

    // 상수 정의
    private static final int BASE_FEE = 1200;

    // 수도 구분별 요금 단가 (코드 1~5)
    private static final int[] RATES = {0, 40, 55, 78, 35, 20}; // 인덱스 0은 미사용
    // 수도 구분별 세금률 (코드 1~5)
    private static final double[] TAX_RATES = {0.0, 0.05, 0.035, 0.025, 0.015, 0.0}; // 인덱스 0은 미사용 (군기관=0.0)
    // 수도 구분 한글명 (코드 1~5)
    private static final String[] CATEGORIES = {"", "가정용", "영업용", "공장용", "관공서", "군기관"};

    // 입력 데이터를 저장할 내부 클래스 (레코드)
    private static class CustomerData {
        String id;
        String name;
        int categoryCode;
        double usage;

        // 계산 결과를 저장할 필드
        double roundedUsage;
        int usedFee;
        int tax;
        int totalFee;
        String remark = "";

        public CustomerData(String id, String name, int categoryCode, double usage) {
            this.id = id;
            this.name = name;
            this.categoryCode = categoryCode;
            this.usage = usage;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<CustomerData> dataList = new ArrayList<>();

        System.out.println("💧 수도 요금 계산 프로그램을 시작합니다.");
        System.out.println("총 10개의 데이터를 입력합니다. (종료: 'q' 또는 10개 입력 완료)");

        // 1. 데이터 입력 받기 (입력 예시 반영)
        for (int i = 0; i < 10; i++) {
            System.out.printf("\n--- %d번째 사용자 정보 입력 ---\n", i + 1);

            System.out.print("사용자 번호 (4자리): ");
            String id = scanner.next();
            if (id.equalsIgnoreCase("q")) break;

            System.out.print("이름: ");
            String name = scanner.next();

            int code = 0;
            while (code < 1 || code > 5) {
                System.out.print("수도 구분 코드 (1~5): ");
                if (scanner.hasNextInt()) {
                    code = scanner.nextInt();
                } else {
                    scanner.next(); // 잘못된 입력 버림
                }
            }

            System.out.print("사용량 (실수, 소수점 이하 1자리): ");
            double usage = scanner.nextDouble();

            dataList.add(new CustomerData(id, name, code, usage));
        }

        // 2. 데이터 처리 및 계산
        for (CustomerData data : dataList) {
            calculateWaterBill(data);
        }

        // 3. 결과 출력
        printResults(dataList);

        scanner.close();
    }

    /**
     * 수도 요금 계산 로직을 수행합니다.
     */
    private static void calculateWaterBill(CustomerData data) {
        // 1. 사용량 정수화 (사사오입/반올림)
        data.roundedUsage = Math.round(data.usage);

        int rate = RATES[data.categoryCode];
        double taxRate = TAX_RATES[data.categoryCode];

        // 2. 사용 금액 계산
        // (주의: 반올림된 사용량을 사용해야 합니다.)
        data.usedFee = (int) data.roundedUsage * rate;

        // 3. 세금 (TAX) 계산 및 1의 자리 절사
        // 세금 = (기본 요금 + 사용 금액) * 세금률
        double taxBase = BASE_FEE + data.usedFee;
        double calculatedTax = taxBase * taxRate;

        // 1의 자리 절사 (버림)
        // 예: 151.7 -> 150, 70.875 -> 70
        data.tax = (int) (Math.floor(calculatedTax / 10.0) * 10);

        // 군기관 (코드 5)은 세금 무료이므로, 0을 확인합니다.
        if (data.categoryCode == 5) {
            data.tax = 0;
            data.remark = "일괄징수"; // 출력 예시에 따른 비고 추가 (임의 설정)
        }


        // 4. 납부 금액 계산
        data.totalFee = BASE_FEE + data.usedFee + data.tax;
    }

    /**
     * 최종 결과를 표 형식으로 출력합니다.
     */
    private static void printResults(List<CustomerData> dataList) {
        System.out.println("\n====================================================================================");
        System.out.println("                                     수 도 요 금");
        System.out.println("====================================================================================");
        System.out.printf("%-5s %-5s %-8s %-8s %-8s %-5s %-8s %s\n",
                "번호", "이름", "구분", "사용량", "사용금액", "TAX", "납부금액", "비고");
        System.out.println("------------------------------------------------------------------------------------");

        for (CustomerData data : dataList) {
            // 출력 형식에 맞게 사용량(실수), 구분(한글), 사용금액, TAX, 납부금액 출력
            System.out.printf("%-5s %-5s %-8s %-8.1f %,-8d %,-5d %,-8d %s\n",
                    data.id,
                    data.name,
                    CATEGORIES[data.categoryCode],
                    data.usage, // 입력된 원본 사용량 출력
                    data.usedFee,
                    data.tax,
                    data.totalFee,
                    data.remark);
        }
        System.out.println("====================================================================================");
    }
}