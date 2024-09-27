import { useState, useEffect } from "react";
import { Bar } from "react-chartjs-2";
import {
  Chart,
  CategoryScale, // X축
  LinearScale, // Y축
  BarElement,
  Title,
  Tooltip,
} from "chart.js";
import { formatTimestamp } from "@/utils/getDate";
import { Histogram } from "@/types/dashboard";

Chart.register(CategoryScale, LinearScale, BarElement, Title, Tooltip);

const HistogramChart = ({
  histogramData,
  histogramInterval, // 시간 간격(분 단위)
  fromDate,
  toDate,
  onBarClick,
}: {
  histogramData: Histogram[] | null;
  histogramInterval: number | null;
  fromDate: string;
  toDate: string;
  onBarClick: (start: string, end: string) => void;
}) => {
  const [chartData, setChartData] = useState<any>(null);

  // 시작 시간과 가장 유사한 분(mm)을 반환
  const roundMinutes = (date: Date, interval: number) => {
    const minutes = date.getMinutes();
    const closestMinutes = Array.from(
      { length: Math.ceil(60 / interval) },
      (_, i) => i * interval
    );

    const closest = closestMinutes.reduce((prev, curr) =>
      Math.abs(curr - minutes) < Math.abs(prev - minutes) ? curr : prev
    );

    date.setMinutes(closest, 0, 0);
    return date;
  };

  useEffect(() => {
    if (histogramData && histogramInterval) {
      let from = new Date(fromDate);
      const to = new Date(toDate);

      from = roundMinutes(from, histogramInterval);
      const intervalInMs = histogramInterval * 60 * 1000;

      const timeLabels: string[] = [];
      const counts: number[] = [];

      for (
        let current = from;
        current <= to;
        current = new Date(current.getTime() + intervalInMs)
      ) {
        // 시간을 x축으로 사용
        timeLabels.push(formatTimestamp(current.toISOString()));

        // 현재 시간대에 해당하는 데이터 조회
        const matchingData = histogramData.find((item) => {
          const itemTime = new Date(item.timestamp).getTime();
          const currentTime = current.getTime();

          // 현재 시간대를 포함하는 범위 내에서 데이터 조회
          return (
            itemTime >= currentTime && itemTime < currentTime + intervalInMs
          );
        });

        // 시간대별 데이터 할당
        counts.push(matchingData ? matchingData.docCount : 0);
      }

      setChartData({
        labels: timeLabels, // x축 (시간대)
        datasets: [
          {
            data: counts, // y축 (요청 수)
            backgroundColor: "rgba(75, 192, 192, 0.6)",
          },
        ],
      });
    }
  }, [histogramData, histogramInterval]);

  const handleBarClick = (_: any, elements: any[]) => {
    if (histogramInterval && histogramInterval > 10 && elements.length > 0) {
      const index = elements[0].index; // 선택한 막대의 인덱스
      const start = chartData.labels[index]; // 선택한 막대의 시작 시간
      const end = formatTimestamp(
        new Date(
          new Date(start).getTime() + histogramInterval * 60 * 1000
        ).toISOString()
      );

      // 부모로 시작 시간과 종료 시간 전달
      onBarClick(start, end);
    }
  };

  return (
    <div className="w-full h-48 border overflow-x-auto">
      {chartData ? (
        <Bar
          data={chartData}
          options={{
            plugins: {
              legend: {
                display: false,
              },
            },
            onClick: handleBarClick,
            scales: {
              x: {
                // x축 설정
                title: {
                  display: true,
                },
                ticks: {
                  maxRotation: 0,
                  minRotation: 0,
                  callback: function (value, index, values) {
                    const label = chartData.labels[index];
                    const date = new Date(label);

                    if (histogramInterval && histogramInterval <= 60) {
                      return date.toLocaleTimeString("ko-KR", {
                        hour: "2-digit",
                        minute: "2-digit",
                        hour12: false, // 24시간 형식으로 표시
                      });
                    } else {
                      return date.toLocaleDateString("ko-KR", {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit",
                      });
                    }
                  },
                },
              },
            },
            responsive: true,
            maintainAspectRatio: false,
          }}
        />
      ) : (
        "Loading chart..."
      )}
    </div>
  );
};

export default HistogramChart;
