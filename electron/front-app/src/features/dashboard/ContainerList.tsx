import React, { useState, useEffect, useMemo, useCallback } from "react";
import { MdKeyboardArrowDown, MdKeyboardArrowUp } from "react-icons/md";
import { useDockerStore } from "../../stores/dockerStore";
import Dockerode from "dockerode";

const MAX_LOGS_PER_CONTAINER = 1000;
const LOG_CLEANUP_INTERVAL = 15 * 60 * 1000; // 15 minutes
const LOG_RETENTION_PERIOD = 24 * 60 * 60 * 1000; // 24 hours

interface ParsedLog {
  timestamp: string;
  message: string;
}

const ContainerList: React.FC = () => {
  const [selectedContainerIds, setSelectedContainerIds] = useState<string[]>(
    []
  );
  const dockerContainers = useDockerStore((state) => state.dockerContainers);
  const [logData, setLogData] = useState<Record<string, ParsedLog[]>>({});

  const addLog = useCallback((containerId: string, newLog: ParsedLog) => {
    setLogData((prevData) => {
      const containerLogs = prevData[containerId] || [];
      const updatedLogs = [...containerLogs, newLog];
      if (updatedLogs.length > MAX_LOGS_PER_CONTAINER) {
        updatedLogs.shift();
      }
      return { ...prevData, [containerId]: updatedLogs };
    });
  }, []);

  useEffect(() => {
    const { onLogStream, onLogError } = window.electronAPI;

    const handleLog = (data: { containerId: string; log: string }) => {
      const parsedLog = parseLog(data.log);
      addLog(data.containerId, parsedLog);
    };

    const handleError = (data: { containerId: string; error: string }) => {
      const errorLog = {
        message: `Error: ${data.error}`,
        timestamp: new Date().toISOString(),
      };
      addLog(data.containerId, errorLog);
    };

    onLogStream(handleLog);
    onLogError(handleError);

    const cleanupInterval = setInterval(() => {
      const now = Date.now();
      setLogData((prevData) => {
        const newData = { ...prevData };
        Object.keys(newData).forEach((containerId) => {
          newData[containerId] = newData[containerId].filter(
            (log) =>
              now - new Date(log.timestamp).getTime() < LOG_RETENTION_PERIOD
          );
        });
        return newData;
      });
    }, LOG_CLEANUP_INTERVAL);

    return () => {
      clearInterval(cleanupInterval);
    };
  }, [addLog]);

  const toggleContainerSelection = useCallback((containerId: string) => {
    setSelectedContainerIds((prevIds) => {
      if (prevIds.includes(containerId)) {
        return prevIds.filter((id) => id !== containerId);
      } else {
        return [...prevIds, containerId];
      }
    });
  }, []);

  const parseLog = (log: string): ParsedLog => {
    const logPattern = /^(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z)\s(.*)$/;
    const match = log.match(logPattern);
    if (match) {
      return {
        timestamp: match[1],
        message: match[2],
      };
    }
    return {
      timestamp: new Date().toISOString(),
      message: log,
    };
  };

  const shortenImageName = (imageName: string) => {
    const parts = imageName.split("/");
    return parts[parts.length - 1].split(":")[0];
  };

  const formatCreatedTime = (created: number) => {
    const date = new Date(created * 1000);
    return `${date.toLocaleDateString()} ${date.toLocaleTimeString()}`;
  };

  const renderPorts = (ports: Dockerode.Port[]) => {
    if (!ports || ports.length === 0) return <span>No Ports</span>;
    return (
      <ul className="list-none p-0">
        {ports.map((port, index) => (
          <li key={`${port.PrivatePort}-${port.PublicPort}-${index}`}>
            {port.PrivatePort} : {port.PublicPort || "N/A"} ({port.Type})
          </li>
        ))}
      </ul>
    );
  };

  const Logs = ({ logs }: { logs: ParsedLog[] }) => {
    return (
      <div
        className="custom-scrollbar"
        style={{ height: "200px", overflowY: "scroll" }}
      >
        {logs.map((log, index) => (
          <div
            key={index}
            className={`text-xs ${
              log.message.toLowerCase().includes("error")
                ? "text-color-8"
                : "text-color-5"
            }`}
          >
            <span className="text-gray-500 mr-2">{log.timestamp}</span>
            <span>{log.message}</span>
          </div>
        ))}
      </div>
    );
  };

  const ContainerRow = useMemo(
    () =>
      ({ container }: { container: DockerContainer }) => {
        const { Id, Names, Image, Created, State, Ports } = container;
        const isSelected = selectedContainerIds.includes(Id);

        return (
          <>
            <tr className="hover:bg-gray-50">
              <td className="py-2 px-4 text-sm text-gray-900">
                {Names[0].slice(1)}
              </td>
              <td className="py-2 px-4 text-sm text-gray-900" title={Image}>
                {shortenImageName(Image)}
              </td>
              <td className="py-2 px-4 text-xs text-gray-900">
                {formatCreatedTime(Created)}
              </td>
              <td className="py-2 px-4 text-sm text-gray-900">
                {renderPorts(Ports)}
              </td>
              <td className="py-2 px-4 text-sm text-gray-900">{State}</td>
              <td className="py-2 px-4 text-sm text-gray-900">
                <button
                  onClick={() => toggleContainerSelection(Id)}
                  className="flex items-center justify-center p-2 hover:bg-gray-200 rounded"
                >
                  {isSelected ? (
                    <MdKeyboardArrowUp className="text-gray-600" />
                  ) : (
                    <MdKeyboardArrowDown className="text-gray-600" />
                  )}
                </button>
              </td>
            </tr>
            {isSelected && (
              <tr>
                <td colSpan={6} className="p-4 bg-gray-100">
                  <Logs logs={logData[Id] || []} />
                </td>
              </tr>
            )}
          </>
        );
      },
    [selectedContainerIds, logData, toggleContainerSelection]
  );

  // if (dockerContainers.length === 0) {
  //   return (
  //     <div className="flex flex-col items-center justify-center mt-8">
  //       <p className="text-center text-gray-700">
  //         현재 배포중인 서비스가 없습니다
  //       </p>
  //       <div className="mt-4 flex text-gray-400 text-sm ">
  //         <div className="text-gray-400 text-sm ">
  //           서비스 할당을 기다려주세요
  //         </div>
  //       </div>
  //     </div>
  //   );
  // }

  return (
    <div className="flex flex-col h-[calc(100vh-200px)]">
      <div className="overflow-hidden rounded-lg custom-scrollbar">
        <table className="min-w-full divide-y divide-gray-300">
          <thead className="sticky z-10 top-0 text-sm bg-white-gradient border-b">
            <tr>
              <th className="p-1">Name</th>
              <th className="p-1">Image</th>
              <th className="p-1">Created</th>
              <th className="p-1">Ports</th>
              <th className="p-1">State</th>
              <th className="p-1">Logs</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 bg-white">
            {dockerContainers.map((container) => (
              <ContainerRow key={container.Id} container={container} />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default React.memo(ContainerList);
